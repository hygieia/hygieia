package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.repository.DashboardRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BusCompOwnerServiceImpl implements BusCompOwnerService{
    private static final Logger LOGGER = LoggerFactory.getLogger(BusCompOwnerServiceImpl.class);
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
    public void assignOwnerToDashboards(String firstName, String middleName, String lastName, Authentication authentication){

        if(firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()){
            /**
             * returns logged in user as Iterable<Owner> for insert
             */
            Owner owner = getUserInformation(authentication);
            /**
             * Returns List of Business Service ObjectIds where give firstName and lastName are found
             * as an Owner for the Service
             */
            List<String> businessServiceList = getBusinessServiceList(firstName, lastName.toLowerCase());
            /**
             * Returns list of Dashboard that are tied to the above found Business Service ObjectIds
             */
            Iterable<Dashboard> dashboards = dashboardRepository.findAllByConfigurationItemBusServNameIn(businessServiceList);
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
                    LOGGER.info("Dashboard Owner updated: " + dashboard.getTitle());
                    List<Owner> dashboardOwners = dashboard.getOwners();
                    dashboardOwners.add(owner);
                    dashboardService.updateOwners(id, dashboardOwners);
                }
            }
        }
    }

    /**
     *
     * @return Owner logged in user
     */
    private Owner getUserInformation(Authentication authentication){
        AuthType authType = (AuthType)authentication.getDetails();
        Owner owner = new Owner(authentication.getName(), authType);

        return owner;
    }
    /**
     *  Takes First name and last name and returns any Business Services where one of the 4 owner fields match the input
     *
     * @param firstName
     * @param lastName
     * @return returns business service ObjectId list
     */
    private List<String> getBusinessServiceList(String firstName, String lastName){
        List<String> businessServiceList = new ArrayList<>();
        List<Cmdb> cmdbs = cmdbService.getAllBusServices();

        /**
         Defining search parameters
         */
        Predicate<Cmdb> supportOwnerLn = p -> p.getSupportOwner() != null ? p.getSupportOwner().toLowerCase().contains(lastName) : false;
        Predicate<Cmdb> serviceOwnerLn = p -> p.getAppServiceOwner() != null ? p.getAppServiceOwner().toLowerCase().contains(lastName) : false;
        Predicate<Cmdb> developmentOwnerLn = p -> p.getDevelopmentOwner() != null ? p.getDevelopmentOwner().toLowerCase().contains(lastName) : false;
        Predicate<Cmdb> businessOwnerLn = p -> p.getBusinessOwner() != null ? p.getBusinessOwner().toLowerCase().contains(lastName) : false;
        /**
         * Combining search parameters into one predicate OR search
         */
        Predicate<Cmdb> fullPredicate = supportOwnerLn.or(serviceOwnerLn).or(developmentOwnerLn).or(businessOwnerLn);
        /**
         * Executes search based on parameters created above and returns matching list
         */
        List<Cmdb> matching = cmdbs.stream().filter(fullPredicate).collect(Collectors.toList());


        for(Cmdb cmdb: matching){
            String businessServiceApp =  cmdb.getConfigurationItem();

            boolean serviceOwnerMatch = doesMatchFullName(firstName, cmdb.getAppServiceOwner());
            boolean businessOwnerMatch = doesMatchFullName(firstName, cmdb.getBusinessOwner());
            boolean supportOwnerMatch = doesMatchFullName(firstName, cmdb.getSupportOwner());
            boolean developmentOwnerMatch = doesMatchFullName(firstName, cmdb.getDevelopmentOwner());

            if((serviceOwnerMatch || businessOwnerMatch || supportOwnerMatch || developmentOwnerMatch)
                    && !businessServiceList.contains(businessServiceApp)){
                businessServiceList.add(businessServiceApp);
            }
        }

        return businessServiceList;
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
        if(firstName != null && !firstName.isEmpty() && fullName != null && !fullName.isEmpty()){
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
