package com.avispl.symphony.dal.avdevices.power.surgex.defenderseries;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.avdevices.power.surgex.defenderseries.common.constants.Constant;


/**
 * Unit tests for the {@code SurgeXDefenderCommunicator} class.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
class SurgeXDefenderCommunicatorTest {
	private ExtendedStatistics extendedStatistics;
	private SurgeXDefenderCommunicator surgeXDefenderCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		this.surgeXDefenderCommunicator = new SurgeXDefenderCommunicator();
		this.surgeXDefenderCommunicator.setHost("");
		this.surgeXDefenderCommunicator.setPort(80);
		this.surgeXDefenderCommunicator.setLogin("");
		this.surgeXDefenderCommunicator.setPassword("");
		this.surgeXDefenderCommunicator.init();
		this.surgeXDefenderCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		this.surgeXDefenderCommunicator.disconnect();
		this.surgeXDefenderCommunicator.destroy();
	}

	@Test
	void testGetMultipleStatistics() throws Exception {
		String historicalProperties = "InternalTemperature(C), LineVoltage(V), Current(A), Power(W)";
		this.surgeXDefenderCommunicator.setHistoricalProperties(historicalProperties);
		this.extendedStatistics = (ExtendedStatistics) this.surgeXDefenderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = this.extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> controllableProperties = this.extendedStatistics.getControllableProperties();
		Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();

		Assertions.assertEquals(
				historicalProperties.split(Constant.COMMA).length,
				this.surgeXDefenderCommunicator.getHistoricalProperties().split(Constant.COMMA).length
		);
		this.verifyStatistics(statistics);
		controllableProperties.forEach(Assertions::assertNotNull);
		dynamicStatistics.forEach(Assertions::assertNotNull);
	}

	@Test
	void testControlOutletGroupReboot() throws Exception {
		this.extendedStatistics = (ExtendedStatistics) this.surgeXDefenderCommunicator.getMultipleStatistics().get(0);
		String groupName = "OutletGroup_01";
		String controlProperty = groupName + "#Reboot";
		ControllableProperty controllableProperty = new ControllableProperty(controlProperty, "1", null);
		this.surgeXDefenderCommunicator.controlProperty(controllableProperty);
		this.extendedStatistics = (ExtendedStatistics) this.surgeXDefenderCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statisticsList = this.extendedStatistics.getStatistics();

		Map<String, String> statisticOutletGroup = this.filterGroupStatistics(statisticsList, groupName);
		List<AdvancedControllableProperty> controllableProperties = this.extendedStatistics.getControllableProperties().stream()
				.filter(property -> property.getName().startsWith(groupName)).collect(Collectors.toList());
		this.verifyStatistics(statisticOutletGroup);
		Assertions.assertEquals(0, controllableProperties.size());
	}

	private void verifyStatistics(Map<String, String> statistics) {
		Map<String, Map<String, String>> groups = new LinkedHashMap<>();
		groups.put(Constant.GENERAL_GROUP, this.filterGroupStatistics(statistics, null));
		groups.put(Constant.ADAPTER_METADATA_GROUP, this.filterGroupStatistics(statistics, Constant.ADAPTER_METADATA_GROUP));
		groups.put(Constant.NETWORK_GROUP, this.filterGroupStatistics(statistics, Constant.NETWORK_GROUP));
		groups.put(Constant.OUTLET_GROUP, this.filterGroupStatistics(statistics, Constant.OUTLET_GROUP));
		groups.put(Constant.GROUP_OUTLET_GROUP, this.filterGroupStatistics(statistics, Constant.GROUP_OUTLET_GROUP));

		for (Map<String, String> initGroup : groups.values()) {
			for (Map.Entry<String, String> initStatistics : initGroup.entrySet()) {
				Assertions.assertNotNull(initStatistics.getValue(), "Value is null with property: " + initStatistics.getKey());
			}
		}
	}

	private Map<String, String> filterGroupStatistics(Map<String, String> statistics, String groupName) {
		return statistics.entrySet().stream()
				.filter(e -> {
					if (groupName == null) {
						return !e.getKey().contains("#");
					} else if (groupName.equals(Constant.OUTLET_GROUP)) {
						return e.getKey().startsWith(groupName) && !e.getKey().contains("Group");
					}
					return e.getKey().startsWith(groupName);
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
