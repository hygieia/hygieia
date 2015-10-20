package com.capitalone.dashboard.client.story;

import com.capitalone.dashboard.client.DataClientSetup;
import com.capitalone.dashboard.datafactory.versionone.VersionOneDataFactoryImpl;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.DateUtil;
import com.capitalone.dashboard.util.FeatureSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Implemented class which is extended by children to perform actual
 * source-system queries as a service and to update the MongoDB in accordance.
 *
 * @author kfk884
 *
 */
@Component
public abstract class FeatureDataClientSetupImpl implements DataClientSetup {
	private static Log logger = LogFactory
			.getLog(FeatureDataClientSetupImpl.class);
	protected final FeatureSettings featureSettings;
	protected final FeatureCollectorRepository featureCollectorRepository;
	protected final VersionOneDataFactoryImpl vOneApi;
	protected String todayDateISO;
	protected String query;
	protected Class<?> objClass;
	protected String returnDate;
	protected FeatureRepository featureRepo;

	/**
	 * Constructs the feature data collection based on system settings.
	 *
	 * @param featureSettings
	 *            Feature collector system settings
	 * @param featureCollectorRepository
	 */
	public FeatureDataClientSetupImpl(FeatureSettings featureSettings,
			FeatureRepository featureRepository,
			FeatureCollectorRepository featureCollectorRepository, VersionOneDataFactoryImpl vOneApi) {
		super();
		logger.debug("Constructing data collection for the feature widget...");

		this.featureSettings = featureSettings;
		this.featureRepo = featureRepository;
		this.featureCollectorRepository = featureCollectorRepository;
		this.vOneApi = vOneApi;
		returnDate = featureSettings.getMasterStartDate();
		setTodayDateISO(DateUtil.toISODateFormat(DateUtil.getTodayNoTime()));
	}

	/**
	 * This method is used to update the database with model defined in the
	 * collector model definitions.
	 *
	 * @see Story
	 */
	public void updateObjectInformation() {
		long start = System.nanoTime();
		int pageIndex = 0;
		int pageSize = this.featureSettings.getPageSize();
		vOneApi.setPageSize(pageSize);
		JSONArray outPutMainArray = new JSONArray();
		JSONArray tmpDetailArray = new JSONArray();
		try {
			vOneApi.buildBasicQuery(query);
			vOneApi.buildPagingQuery(0);
			outPutMainArray = vOneApi.getPagingQueryResponse();
			if (outPutMainArray == null) {
				throw new Exception("FAILED: Script Completed with Error");
			}
			tmpDetailArray = (JSONArray) outPutMainArray.get(0);
			while (tmpDetailArray.size() > 0) {
				updateMongoInfo(tmpDetailArray);
				tmpDetailArray.clear();
				pageIndex = pageIndex + pageSize;
				vOneApi.buildPagingQuery(pageIndex);
				outPutMainArray.clear();
				outPutMainArray = vOneApi.getPagingQueryResponse();
				if (outPutMainArray == null) {
					logger.info("FAILED: Script Completed with Error");
					throw new Exception("FAILED: Script Completed with Error");
				}
				tmpDetailArray = (JSONArray) outPutMainArray.get(0);
			}
		} catch (Exception e) {
			logger.error("Unexpected error in VersionOne paging request of "
					+ e.getClass().getName() + "\n[" + e.getMessage() + "]");
		}

		double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
		logger.info("Process took :" + elapsedTime + " seconds to update");
		System.out.println("Process took :" + elapsedTime
				+ " seconds to update");

	}

	/**
	 * Generates and retrieves the local server time stamp in Unix Epoch format.
	 *
	 * @param unixTimeStamp
	 *            The current millisecond value of since the Unix Epoch
	 * @return Unix Epoch-formatted time stamp for the current date/time
	 */
	public String getLocalTimeStampFromUnix(long unixTimeStamp) {
		long unixSeconds = unixTimeStamp;

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
		String date = sdf.format(unixSeconds);
		logger.debug(unixSeconds + "==>" + date);

		return date;
	}

	/**
	 * Generates and retrieves the change date that occurs a minute prior to the
	 * specified change date in ISO format.
	 *
	 * @param changeDateISO
	 *            A given change date in ISO format
	 * @return The ISO-formatted date/time stamp for a minute prior to the given
	 *         change date
	 */
	public String getChangeDateMinutePrior(String changeDateISO) {
		int priorMinutes = this.featureSettings.getScheduledPriorMin();
		return DateUtil.toISODateRealTimeFormat(DateUtil.getDatePriorToMinutes(
				DateUtil.fromISODateTimeFormat(changeDateISO), priorMinutes));
	}

	/**
	 * Generates and retrieves the sprint start date in ISO format.
	 *
	 * @return The ISO-formatted date/time stamp for the sprint start date
	 */
	public String getSprintBeginDateFilter() {
		int priorDays = this.featureSettings.getSprintDays();
		return DateUtil.toISODateRealTimeFormat(DateUtil.getDatePriorToNDays(
				DateUtil.getDateNoTime(new Date()), priorDays));
	}

	/**
	 * Generates and retrieves the sprint end date in ISO format.
	 *
	 * @return The ISO-formatted date/time stamp for the sprint end date
	 */
	public String getSprintEndDateFilter() {
		int afterDays = this.featureSettings.getSprintDays();
		return DateUtil.toISODateRealTimeFormat(DateUtil.addDays(
				DateUtil.getDateNoTime(new Date()), afterDays));
	}

	/**
	 * Generates and retrieves the difference between the sprint start date and
	 * the sprint end date in ISO format.
	 *
	 * @return The ISO-formatted date/time stamp for the sprint start date
	 */
	public String getSprintDeltaDateFilter() {
		int priorDeltaDays = this.featureSettings.getSprintEndPrior();
		return DateUtil.toISODateRealTimeFormat(DateUtil.getDatePriorToNDays(
				DateUtil.getDateNoTime(new Date()), priorDeltaDays));
	}

	/**
	 * Accessor method for today's date in ISO format
	 */
	public String getTodayDateISO() {
		return todayDateISO;
	}

	/**
	 * Mutator method for setting today's date in ISO format
	 */
	public void setTodayDateISO(String todayDateISO) {
		this.todayDateISO = todayDateISO;
	}

	/**
	 * Retrieves the maximum change date for a given query.
	 *
	 * @return A list object of the maximum change date
	 */
	public String getMaxChangeDate() {
		List<Feature> response = null;
		String data = null;

		try {
			response = featureRepo
					.getFeatureMaxChangeDate(featureCollectorRepository
							.findByName("VersionOne").getId(), featureSettings
							.getDeltaStartDate());
			if (response.size() > 0) {
				data = response.get(0).getChangeDate();
			}
		} catch (NullPointerException npe) {
			logger.debug("No data was currently available in the local database that corresponded to a max change date\nReturning null");
		} catch (Exception e) {
			logger.error("There was a problem retrieving or parsing data from the local repository while retrieving a max change date\nReturning null");
		}

		if (data != null) {
			return data;
		} else {
			return null;
		}

	}

	/**
	 * Abstract method required by children methods to update the MongoDB with a
	 * JSONArray received from the source system back-end.
	 *
	 * @param tmpMongoDetailArray
	 *            A JSON response in JSONArray format from the source system
	 * @return
	 */
	protected abstract void updateMongoInfo(JSONArray tmpMongoDetailArray);
}
