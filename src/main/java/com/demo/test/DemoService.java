package com.demo.test;

import com.hangyin.smart.smartcore.model.SmartCoreShareModel;
import com.hangyin.smart.smartcore.model.SmartCoreDeviceInfoModel;
import com.hangyin.smart.smartcore.model.SmartCoreDeviceModel;
import com.hangyin.smart.smartcore.model.SmartCorePropertyModel;
import com.hangyin.smart.smartcore.service.ISmartCoreDevicePropertyValueChangeNotifyFunction;
import com.hangyin.smart.smartcore.service.ISmartCoreDeviceService;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *  plugin demo
 *
 * @author hang.yin
 * @date 2020-04-26
 */
@SuppressWarnings("unused")
public class DemoService implements ISmartCoreDeviceService {
    private ISmartCoreDevicePropertyValueChangeNotifyFunction notifyFunction;

    private SmartCoreDeviceModel device;
    private Boolean isRunning = true;

    @Override
    public Boolean init(Map<String, SmartCoreShareModel> shareMap, ISmartCoreDevicePropertyValueChangeNotifyFunction notifyFunction, String deviceId, Map params) {
        System.out.println(deviceId + " init invoke.");
        if(StringUtils.isBlank(deviceId)) {
            return false;
        }

        this.notifyFunction = notifyFunction;

        device = new SmartCoreDeviceModel(deviceId)
            .setFriendlyName((String) params.get("friendlyName"))
            .setInfo(new SmartCoreDeviceInfoModel("SmartCore", "test", deviceId))
            .setType("light")
            .addProperty("power", new SmartCorePropertyModel("电源", true, null, false, true))
            .addProperty("mode", new SmartCorePropertyModel("模式", 1, "[1-3]", false, true))
            .addProperty("ledShowContent", new SmartCorePropertyModel("显示内容", "hello", null, false, true));

        new Thread(() -> {
            while(isRunning) {
                try {
                    Thread.sleep(12 * 1000);

                    Boolean currValue = (Boolean) this.device.getPropertyValue("power");
                    this.device.setPropertyValue("power", !currValue, (i, v) -> true, this.notifyFunction, false);
                } catch (Exception ignored){}
            }
        }).start();

        return true;
    }

    @Override
    public Boolean close(Map params) {
        isRunning = false;
        System.out.println(this.device.getDeviceId() + " close invoke.");
        return true;
    }

    @Override
    public SmartCoreDeviceModel getDevice() {
        return this.device;
    }

    @Override
    public String getDeviceId() {
        return this.device.getDeviceId();
    }

    @Override
    public String getDeviceFriendlyName() {
        return this.device.getFriendlyName();
    }

    @Override
    public String getDeviceType() {
        return this.device.getType();
    }

    @Override
    public SmartCoreDeviceInfoModel getDeviceInfo() {
        return this.device.getInfo();
    }

    @Override
    public Map<String, SmartCorePropertyModel> getDeviceProperties() {
        return this.device.getProperties();
    }

    @Override
    public SmartCorePropertyModel getProperty(String propertyId) {
        return this.device.getProperties().get(propertyId);
    }

    @Override
    public Object getPropertyValue(String propertyId) {
        return this.device.getPropertyValue(propertyId);
    }

    @Override
    public Object getPropertyFriendlyName(String propertyId) {
        return this.device.getPropertyFriendlyName(propertyId);
    }

    @Override
    public Boolean setPropertyValue(String propertyId, Object propertyValue) {
        return this.device.setPropertyValue(propertyId, propertyValue, (i, v) -> true, this.notifyFunction, true);
    }

    @Override
    public Map<String, Object> getPropertyValueMap() {
        return this.device.getProperties().entrySet().stream().collect(HashMap::new, (m, d) -> m.put(d.getKey(), d.getValue().getValue()), HashMap::putAll);
    }

    @Override
    public Map<String, Boolean> setPropertyValueMap(Map<String, Object> values) {
        Map<String, Boolean> r = new HashMap<>();
        for(String propertyId: values.keySet()) {
            r.put(propertyId, this.setPropertyValue(propertyId, values.get(propertyId)));
        }
        return r;
    }

    @Override
    public Boolean operation(String operationType) {
        System.out.println(operationType);
        return null;
    }
}
