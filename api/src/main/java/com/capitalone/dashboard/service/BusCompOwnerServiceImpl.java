package com.capitalone.dashboard.service;

import com.capitalone.dashboard.auth.AuthenticationUtil;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.repository.DashboardRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BusCompOwnerServiceImpl implements BusCompOwnerService{

    private final CmdbService cmdbService;
    private final DashboardRepository dashboardRepository;
    private final DashboardService dashboardService;

    @Autowired
    public BusCompOwnerServiceImpl(CmdbService cmdbService,
                                   DashboardRepository dashboardRepository,
                                   DashboardService dashboardService) {
        this.cmdbService = cmdbService;
        this.dashboardRepository = dashboardRepository;
        this.dashboardService = dashboardService;
    }
    @Override
    public void assignOwnerToDashboards(String firstName, String middleName, String lastName){
        /**
         * returns logged in user as Iterable<Owner> for insert
         */
        Iterable<Owner> owner = getUserInformation();
        /**
         * Returns List of Business Service ObjectIds where give firstName and lastName are found
         * as an Owner for the Service
         */
        Iterable<ObjectId> businessServiceObjectIdList = getBusinessServiceList(firstName, lastName);
        /**
         * Returns list of Dashboard that are tied to the above found Business Service ObjectIds
         */
        Iterable<Dashboard> dashboards = dashboardRepository.findAllByConfigurationItemBusServObjectIdIn(businessServiceObjectIdList);
        /**
         * Returns list of Dashboard ObjectIds where the logged in user is an owner
         */
        List<ObjectId> ownedDashboardObjectIds = dashboardService.getOwnedDashboardsObjectIds();
        /**
         * Loops through list of matching dashBoards and adds logged in user to the board if they are
         * not already a member
         */
        for(Dashboard dashboard: dashboards){
            ObjectId id = dashboard.getId();
            if(ownedDashboardObjectIds != null && !ownedDashboardObjectIds.contains(dashboard.getId())){
                dashboardService.updateOwners(id, owner);
            }
        }
    }

    /**
     *
     * @return Iterable<Owner> logged in user
     */
    private Iterable<Owner> getUserInformation(){
        List<Owner> ownerList = new ArrayList<>();
        Owner owner = new Owner(AuthenticationUtil.getUsernameFromContext(), AuthenticationUtil.getAuthTypeFromContext());
        ownerList.add(owner);
        return ownerList;
    }
    /**
     *  Takes First name and last name and returns any Business Services where one of the 4 owner fields match the input
     *
     * @param firstName
     * @param lastName
     * @return returns business service ObjectId list
     */
    private Iterable<ObjectId> getBusinessServiceList(String firstName, String lastName){
        List<ObjectId> businessServiceObjectIdList = new ArrayList<>();
        List<Cmdb> cmdbs = cmdbService.getAllBusServices();

        /**
         Defining search parameters
         */
        Predicate<Cmdb> supportOwnerLn = p -> p.getSupportOwner().contains(lastName);
        Predicate<Cmdb> serviceOwnerLn = p -> p.getAppServiceOwner().contains(lastName);
        Predicate<Cmdb> developmentOwnerLn = p -> p.getDevelopmentOwner().contains(lastName);
        Predicate<Cmdb> businessOwnerLn = p -> p.getBusinessOwner().contains(lastName);
        /**
         * Combining search parameters into one predicate OR search
         */
        Predicate<Cmdb> fullPredicate = supportOwnerLn.or(serviceOwnerLn).or(developmentOwnerLn).or(businessOwnerLn);
        /**
         * Executes search based on parameters created above and returns matching list
         */
        List<Cmdb> matching = cmdbs.stream().filter(fullPredicate).collect(Collectors.toList());


        for(Cmdb cmdb: matching){
            ObjectId businessServiceObjectId =  cmdb.getId();

            boolean serviceOwnerMatch = doesMatchFullName(firstName, cmdb.getAppServiceOwner());
            boolean businessOwnerMatch = doesMatchFullName(firstName, cmdb.getBusinessOwner());
            boolean supportOwnerMatch = doesMatchFullName(firstName, cmdb.getSupportOwner());
            boolean developmentOwnerMatch = doesMatchFullName(firstName, cmdb.getDevelopmentOwner());

            if((serviceOwnerMatch || businessOwnerMatch || supportOwnerMatch || developmentOwnerMatch)
                    && !businessServiceObjectIdList.contains(businessServiceObjectId)){
                businessServiceObjectIdList.add(businessServiceObjectId);
            }
        }

        return businessServiceObjectIdList;
    }

    /**
     * Takes first name and full name and returns true or false if first name is found in full name
     *
     * @param firstName
     * @param fullName
     * @return true or false if match found
     */
    private boolean doesMatchFullName(String firstName, String fullName){
        boolean matching = false;
        if(firstName != null && !firstName.isEmpty()){
            String firstFromCMDB;
            String[] array = fullName.split(" ");

            firstName = firstName.toLowerCase();
            firstFromCMDB = array[0];
            firstFromCMDB = firstFromCMDB.toLowerCase();
            if(firstFromCMDB.length() < firstName.length()){
                if(firstName.indexOf(firstFromCMDB) != -1){
                    matching = true;
                }
            }else if (firstFromCMDB.indexOf(firstName) != -1){
                matching = true;
            }
        }
        return matching;
    }
}
