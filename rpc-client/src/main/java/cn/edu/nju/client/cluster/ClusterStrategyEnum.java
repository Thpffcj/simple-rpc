package cn.edu.nju.client.cluster;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by thpffcj on 2019/12/14.
 */
public enum  ClusterStrategyEnum {

    RANDOM("Random"),
    WEIGHT_RANDOM("WeightRandom"),
    POLLING("Polling"),
    WEIGHT_POLLING("WeightPolling"),
    HASH("Hash");

    private final String code;

    ClusterStrategyEnum(String code) {
        this.code = code;
    }

    public static ClusterStrategyEnum queryByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        ClusterStrategyEnum strategy = null;
        for (ClusterStrategyEnum strategyEnum : values()) {
            if (StringUtils.equals(code, strategyEnum.getCode())) {
                strategy = strategyEnum;
                break;
            }
        }
        return strategy;
    }

    public String getCode() {
        return code;
    }
}
