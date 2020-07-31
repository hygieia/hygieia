import { Component, OnInit } from '@angular/core';
import { UserDataService } from '../../../services/user-data.service';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-manage-admins',
  templateUrl: './manage-admins.component.html',
  styleUrls: ['./manage-admins.component.scss']
})

export class ManageAdminsComponent implements OnInit {

  error: any = {};
  users: any[] = [];
  userSearch = '';
  authType: any;
  username: any;
  p = 1;

  constructor(private userData: UserDataService, private authService: AuthService) {
    this.authType = this.authService.getAuthType();
    this.username = this.authService.getUserName();
  }
  ngOnInit() {
    this.loadUser();

  }

  loadUser() {
    this.userData.users().subscribe((response: any) => {
      this.users = response;
    });
  }
  promoteUserToAdmin(user) {
    this.userData.promoteUserToAdmin(user).subscribe(
      (response: any) => {
        const index = this.users.indexOf(user);
        this.users[index] = response.data;
        this.loadUser();
      },
      (error) => {
        this.error = error;
      }
    );

  }

  demoteUserFromAdmin(user) {
    this.userData.demoteUserFromAdmin(user).subscribe(
      (response: any) => {
        const index = this.users.indexOf(user);
        this.users[index] = response.data;
        this.loadUser();
      },
      (error) => {
        this.error = error;
      }
    );
  }

}

