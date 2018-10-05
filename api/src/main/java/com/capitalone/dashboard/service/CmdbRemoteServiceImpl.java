package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.repository.CmdbRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.request.CmdbRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CmdbRemoteServiceImpl implements CmdbRemoteService {

    private static final String CONFIGURATION_ITEM = "configurationItem";
    private static final String COMMON_NAME = "commonName";
    private static final String COMPONENT_TYPE = "component";
    private static final String APP_TYPE = "app";

    private final CollectorService collectorService;
    private final CmdbRepository cmdbRepository;
    private final CollectorRepository collectorRepository;


    @Autowired
    public CmdbRemoteServiceImpl(
           CollectorService collectorService,
           CmdbRepository cmdbRepository,
           CollectorRepository collectorRepository) {

        this.collectorService = collectorService;
        this.cmdbRepository = cmdbRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public Cmdb remoteCreate(CmdbRequest request ) throws HygieiaException {

        validateRequest(request);

        updateRelationship(request);

        Cmdb cmdb = requestToCmdb(request);

        List<Collector> collectors = collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName());
        cmdb.setCollectorItemId( buildCollectorItem( request, collectors.get(0) ).getId() );

        return cmdbRepository.save( cmdb );
    }

    /**
     * If ConfigurationItemBusServName is set then update it with the new relationship
     * @param request
     */
    private void updateRelationship( CmdbRequest request ) {
        if( !StringUtils.isEmpty( request.getConfigurationItemBusServName() ) ) {
            Cmdb busServiceItem = cmdbRepository.findByConfigurationItemAndItemType(request.getConfigurationItemBusServName(), APP_TYPE);

            if (busServiceItem.getComponents() == null){
                List<String> components =  new ArrayList<>();
                components.add(request.getConfigurationItem());

                busServiceItem.setComponents(components);
            }else{
                busServiceItem.getComponents().add(request.getConfigurationItem());
            }

            cmdbRepository.save( busServiceItem );
        }
    }

    /**
     * Validates CmdbRequest for errors
     * @param request
     * @throws HygieiaException
     */
    private void validateRequest(CmdbRequest request) throws HygieiaException {
        String busServiceName = request.getConfigurationItemBusServName();
        if(!StringUtils.isEmpty( busServiceName ) && cmdbRepository.findByConfigurationItemAndItemType( busServiceName, APP_TYPE ) == null){
            throw new HygieiaException("Configuration Item " + busServiceName + " does not exist", HygieiaException.BAD_DATA);
        }

        Cmdb cmdb = cmdbRepository.findByConfigurationItemIgnoreCaseOrCommonNameIgnoreCase(request.getConfigurationItem(), request.getCommonName());
        if(cmdb != null){
            throw new HygieiaException("Configuration Item " + cmdb.getConfigurationItem() + " already exists", HygieiaException.DUPLICATE_DATA);
        }

        List<Collector> collectors = collectorRepository.findByCollectorTypeAndName(CollectorType.CMDB, request.getToolName());
        if (CollectionUtils.isEmpty(collectors)) {
            throw new HygieiaException(request.getToolName() + " collector is not available.", HygieiaException.BAD_DATA);
        }
    }

    /**
     * Takes CmdbRequest and converts to Cmdb Object
     * @param request
     * @return Cmdb item
     */
    private Cmdb requestToCmdb( CmdbRequest request ){
        Cmdb cmdb = new Cmdb();
        cmdb.setConfigurationItem( request.getConfigurationItem() );
        cmdb.setConfigurationItemSubType( request.getConfigurationItemSubType() );
        cmdb.setConfigurationItemType( request.getConfigurationItemType() );
        cmdb.setAssignmentGroup( request.getAssignmentGroup() );
        cmdb.setOwnerDept( request.getOwnerDept() );
        cmdb.setCommonName( request.getCommonName() );
        cmdb.setValidConfigItem( true );
        cmdb.setTimestamp( System.currentTimeMillis() );
        cmdb.setItemType( COMPONENT_TYPE );
        return cmdb;
    }

    /**
     * Builds collector Item for new Cmdb item
     * @param request
     * @param collector
     * @return
     */
    private CollectorItem buildCollectorItem( CmdbRequest request, Collector collector ) {

        CollectorItem collectorItem = new CollectorItem();
        collectorItem.setCollectorId( collector.getId() );
        collectorItem.setEnabled( false );
        collectorItem.setPushed( true );
        collectorItem.setDescription( request.getCommonName() );
        collectorItem.setLastUpdated( System.currentTimeMillis() );
        collectorItem.getOptions().put( CONFIGURATION_ITEM, request.getConfigurationItem() );
        collectorItem.getOptions().put( COMMON_NAME, request.getCommonName() );

        return  collectorService.createCollectorItem( collectorItem );
    }


}
