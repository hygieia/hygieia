package com.capitalone.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CaApm;
import com.capitalone.dashboard.repository.CaApmAlertRepository;

@Service
public class CaApmServiceImpl implements CaApmService {

    //private final ComponentRepository componentRepository;
   // private final CollectorRepository collectorRepository;
    private final CaApmAlertRepository caApmAlertRepository;

    @Autowired
    public CaApmServiceImpl(CaApmAlertRepository caApmAlertRepository) {
        this.caApmAlertRepository = caApmAlertRepository;
      //  this.componentRepository = componentRepository;
       // this.collectorRepository = collectorRepository;
    }
       
	@Override
	public Iterable<CaApm> getAlertsByManageModuleName(String manModuleName){
		return caApmAlertRepository.getAlertsByManageModuleName(manModuleName);
	}
	
    
  /*  @Override
    public DataResponse<Iterable<CaApmCollectorItem>> getAllManageModules(CaApmRequest request) {

        Component component = componentRepository.findOne(request.getComponentId());
        CollectorItem item = component.getCollectorItems().get(CollectorType.CaApm).get(0);

        Collector collector = collectorRepository.findOne(item.getCollectorId());
        List<ObjectId> ids = new ArrayList<ObjectId>();
        ids.add(collector.getId());
     	return new DataResponse<Iterable<CaApmCollectorItem>>(ids, collector.getLastExecuted());
    } */ 
    
    
}
