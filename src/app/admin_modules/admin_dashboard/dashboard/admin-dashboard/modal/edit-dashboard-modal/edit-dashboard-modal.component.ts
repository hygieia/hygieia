import { Component, OnInit, Input } from '@angular/core';
import { DashboardDataService } from 'src/app/admin_modules/admin_dashboard/services/dashboard-data.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { WidgetManagerService } from 'src/app/admin_modules/admin_dashboard/services/widget-manager.service';
import * as _ from 'lodash';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';
import { CmdbDataService } from 'src/app/admin_modules/admin_dashboard/services/cmdb-data.service';
import { AdminDashboardService } from 'src/app/admin_modules/admin_dashboard/services/dashboard.service';
import { map, debounceTime, distinctUntilChanged, switchMap, catchError, tap } from 'rxjs/operators';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DashboardItem } from '../../model/dashboard-item';
import { Observable, of } from 'rxjs';

@Component({
    selector: 'app-edit-dashboard-modal',
    templateUrl: './edit-dashboard-modal.component.html',
    styleUrls: ['./edit-dashboard-modal.component.scss']
})
export class EditDashboardModalComponent implements OnInit {

    @Input() public dashboardTitle = '';
    username: string;
    authType: string;
    @Input() dashboardItem: DashboardItem = new DashboardItem();
    activeWidgets: any[];
    scoreSettings: { scoreEnabled: boolean; scoreDisplay: any; };
    tabView: any;
    tabs: { name: string; }[];
    configurationItemBusApp: any;
    configurationItemBusServ: any;
    selectWidgetsDisabled: boolean;
    widgets: {};
    widgetSelections: any = {};
    users: any;
    owners: any;
    error: any;
    dupErroMessage: any;
    selectedWidgets: any;
    cdfForm: FormGroup;
    formBusinessService: FormGroup;
    isSubmit: boolean;
    userSearch = '';
    Object = Object;
    scoreDisplayType = {
        HEADER: 'HEADER',
        WIDGET: 'WIDGET'
    };
    selectHeaderOrWidgetToolTip = 'Dashboard score can either be displayed in header or as a widget.';
    searchconfigItemBus: any;
    allWidgets: unknown[];
    searchconfigItemBusComponent: any;
    noResults = false;
    noResultsCom = false;

    constructor(private dashboardData: DashboardDataService, private authService: AuthService,
                private widgetManager: WidgetManagerService, private userData: UserDataService,
                private cmdbData: CmdbDataService, private dashboardService: AdminDashboardService,
                private fromBulider: FormBuilder, public activeModal: NgbActiveModal) { }

    ngOnInit() {
        this.username = this.authService.getUserName();
        this.authType = this.authService.getAuthType();
        this.dashboardData.owners(this.dashboardItem.id).subscribe(this.processOwnerResponse);
        this.dashboardData.detail(this.dashboardItem.id).subscribe(this.processDashboardDetail);
        this.configurationItemBusServ = this.dashboardItem.configurationItemBusServName;
        this.configurationItemBusApp = this.dashboardItem.configurationItemBusAppName;
        this.tabs = [
            { name: 'Dashboard Title' },
            { name: 'Business Service/ Application' },
            { name: 'Owner Information' },
            { name: 'Widget Management' },
            { name: 'Score' }
        ];
        this.tabView = this.tabs[0].name;
        this.activeWidgets = [];
        this.scoreSettings = {
            scoreEnabled: !!this.dashboardItem.scoreEnabled,
            scoreDisplay: this.dashboardItem.scoreDisplay
        };
        this.cdfForm = this.fromBulider.group({
            dashboardTitle: ['',
                [Validators.required, Validators.minLength(6), Validators.maxLength(50), Validators.pattern(/^[a-zA-Z0-9 ]*$/)]]
        });
        this.formBusinessService = this.fromBulider.group({
            configurationItemBusServ: [''],
            configurationItemBusApp: ['']
        });

        this.getConfigItem('app', '');
        this.getConfigItemComponent('', '');
        setTimeout(() => {
            console.log('dashboardItem' + JSON.stringify(this.dashboardItem));
            this.cdfForm.get('dashboardTitle').setValue(this.getDashboardTitle());
        }, 100);

    }


        searchconfigItemBusServ = (text$: Observable<string>) =>
        text$.pipe(
          debounceTime(200),
          distinctUntilChanged(),
          tap(() => this.noResults = false),
          switchMap(term =>
            this.cmdbData.getConfigItemList('app', { search: term, size: 20 })
            .pipe(
                map( (result: any) => {
                    if (!result || result.length === 0) {
                        this.noResults = true;
                    }
                    return result;
                } ),
              catchError(() => {
                  return of([]);
              }))
          ),
        )

    formatter = (x: { configurationItem: string }) => x.configurationItem;
        searchconfigItemBusApp = (text$: Observable<string>) =>
        text$.pipe(
          debounceTime(200),
          distinctUntilChanged(),
          tap(() => this.noResultsCom = false),
          switchMap(term =>
            this.cmdbData.getConfigItemList('component', { search: term, size: 20 })
            .pipe(
                map( (result: any) => {
                    if (!result || result.length === 0) {
                        this.noResultsCom = true;
                    }
                    return result;
                } ),
              catchError(() => {
                  return of([]);
              }))
          ),
        )

    formatterConfigItemBusApp = (x: { configurationItem: string }) => x.configurationItem;

    get f() { return this.cdfForm.controls; }

    get fB() { return this.formBusinessService.controls; }



    processDashboardDetail = (response) => {
        this.dashboardData.getMyWidget(response.template).subscribe((result: any) => {
            const widgetsSet = new Set();
            result.forEach(ele => {
                console.log('template', ele.template);
                if (ele.widgets) {
                    ele.widgets.forEach(widget => {
                        if (widget.name && widget.options.id) {
                            widgetsSet.add(widget.name);
                        }
                    });
                }
            });
            this.allWidgets = [...widgetsSet];
            this.activeWidgets = [];
            this.widgets = this.widgetManager.getWidgets();
            if (response.template === 'widgets') {
                this.selectWidgetsDisabled = false;
                this.activeWidgets = response.activeWidgets;
            } else {
                this.selectWidgetsDisabled = true;
                this.allWidgets.forEach( widgetName => {
                    this.activeWidgets.push(widgetName);
                });
            }
            this.allWidgets.forEach((widget: any) => {
                this.widgetSelections[widget] = true;
            });
        });
    }

    processUserResponse = (response) => {
        console.log('user :', this.users, response);
        this.users = response;
    }

    processOwnerResponse = (response) => {
        this.owners = response;
        this.userData.users().subscribe(this.processUserResponse);
    }

    isActiveUser = (user) => {
        if (user.authType === this.authType && user.username === this.username) {
            return true;
        }
        return false;
    }

    promoteUserToOwner(user) {
        const index = this.users.indexOf(user);
        if (index > -1) {
            this.owners.push(user);
        }
    }

    demoteUserFromOwner(user) {
        const index = this.owners.indexOf(user);
        if (index > -1) {
            this.owners.splice(index, 1);
        }
    }

    saveForm() {
        console.log('tabview ', this.tabView);
        switch (this.tabView) {
            case 'Dashboard Title':
                this.submit('');
                break;
            case 'Business Service/ Application':
                this.submitBusServOrApp('');
                break;
            case 'Owner Information':
                console.log('ownerFormSubmit');
                this.ownerFormSubmit('');
                break;
            case 'Widget Management':
                this.saveWidgets('');
                break;
            case 'Score':
                this.submitScoreSettings('');
                break;
        }
    }

    submit(form) {
        if (this.cdfForm.valid) {
            this.isSubmit = true;
            this.renameSubmit()
                .subscribe(() => {
                    this.activeModal.dismiss();
                    this.isSubmit = false;
                }, (error: any) => {
                    this.isSubmit = false;
                    this.activeModal.close();
                    this.error = error.data;
                });
        }
    }

    renameSubmit() {
        console.log('value', this.cdfForm.get('dashboardTitle'));
        return this.dashboardData.renameDashboard(this.dashboardItem.id, this.cdfForm.get('dashboardTitle').value)
            .pipe(map((response: any) => {
                return response;
            }));
    }
    ownerFormSubmit(form) {
        console.log('form', form);
        this.ownerSubmit()
            .subscribe(() => {
                this.activeModal.dismiss();
            }, (error: any) => {
                this.activeModal.close();
                this.error = error.data;
            });
    }
    ownerSubmit() {
        console.log('users save', this.owners);
        return this.dashboardData.updateOwners(this.dashboardItem.id, this.prepareOwners(this.owners))
            .pipe(map((response: any) => {
                return response;
            }));
    }

    prepareOwners(owners) {
        const putData = [];

        owners.forEach((owner) => {
            putData.push({ username: owner.username, authType: owner.authType });
        });

        return putData;
    }

    submitBusServOrApp(form) {
        if (this.formBusinessService.valid) {
            const submitData = {
                configurationItemBusServName: this.formBusinessService.get('configurationItemBusServ').value.configurationItem,
                configurationItemBusAppName: this.formBusinessService.get('configurationItemBusApp').value.commonName
            };
            this.dashboardData
                .updateBusItems(this.dashboardItem.id, submitData)
                .subscribe((data: any) => {
                    this.activeModal.dismiss();
                }
                    , (error: any) => {
                        if (error) {
                            this.dupErroMessage = error;
                        }
                    });
        }

    }

    getConfigItem(type, filter) {
        console.log('getConfigItem', type, filter);
        return this.cmdbData.getConfigItemList(type, { search: filter, size: 20 })
            .subscribe((response) => {
                console.log('getConfigItem', response);
                this.searchconfigItemBus = response;
            });
    }

    getConfigItemComponent(type, filter) {
        console.log('getConfigItem', type, filter);
        return this.cmdbData.getConfigItemList('component', { search: filter, size: 20 })
            .subscribe((response) => {
                console.log('getConfigItem', response);
                this.searchconfigItemBusComponent = response;
            });
    }
    getDashboardTitle() {
        return this.dashboardService.getDashboardTitleOrig(this.dashboardItem);
    }

    getBusAppToolText() {
        return this.dashboardService.getBusAppToolTipText();
    }

    getBusSerToolText() {
        return this.dashboardService.getBusSerToolTipText();
    }
    tabToggleView(index) {
        this.dupErroMessage = '';
        this.tabView = typeof this.tabs[index] === 'undefined' ? this.tabs[0].name : this.tabs[index].name;
    }
    resetFormValidation(form) {
        this.dupErroMessage = '';
        form.configurationItemBusServ.$setValidity('dupBusServError', true);
        if (form.configurationItemBusApp) {
            form.configurationItemBusApp.$setValidity('dupBusAppError', true);
        }

    }
    isValidBusServName() {
        let valid = true;
        if (this.dashboardItem.configurationItemBusServName !== undefined && !this.dashboardItem.validServiceName) {
            valid = false;
        }
        return valid;
    }
    isValidBusAppName() {
        let valid = true;
        if (this.dashboardItem.configurationItemBusAppName !== undefined && !this.dashboardItem.validAppName) {
            valid = false;
        }
        return valid;
    }

    // Save template - after edit
    saveWidgets(form) {
        this.findSelectedWidgets();
        const submitData = {
            activeWidgets: this.selectedWidgets
        };
        console.log('this.selectedWidgets 337', this.selectedWidgets);
        this.dashboardData
            .updateDashboardWidgets(this.dashboardItem.id, submitData)
            .subscribe((data) => {
            }
                , (error: any) => {
                    const msg = 'An error occurred while editing dashboard';
                    this.swal(msg);
                });
    }

    // find selected widgets and add it to collection
    findSelectedWidgets() {
        this.selectedWidgets = [];
        console.log('widgetSelections 360', this.widgetSelections);

        Object.entries(this.widgetSelections).map((items: any) => {
            const s = this.widgetSelections[items[0]];
            if (s) {
                this.selectedWidgets.push(items[0]);
            }
        });
    }

    onConfigurationItemBusAppSelect(value) {
        this.configurationItemBusApp = value;
    }

    submitScoreSettings(form) {
        if (this.scoreSettings.scoreEnabled) {
            this.dashboardData
                .updateDashboardScoreSettings(this.dashboardItem.id, this.scoreSettings.scoreEnabled, this.scoreSettings.scoreDisplay)
                .subscribe((data: any) => {
                    this.activeModal.dismiss();
                },
                    (error: any) => {
                        const msg = 'An error occurred while editing dashboard';
                        this.swal(msg);
                    });
        }
    }
    swal(info: any) {
        console.log('swal', 'info', info);
    }

}
