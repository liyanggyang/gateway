package com.github.cwdtom.gateway.mapping;

import com.github.cwdtom.gateway.constant.LoadBalanceConstant;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * proxy mapper
 *
 * @author chenweidong
 * @since 1.4.0
 */
@ToString
@Slf4j
public class Mapper {
    /**
     * host
     */
    private String host;
    /**
     * weight
     */
    private int weight;
    /**
     * exception count
     */
    private AtomicInteger exceptionCount;
    /**
     * target url
     */
    private String target;

    public Mapper(String host, String target, Integer weight) {
        this.host = host;
        this.target = target;
        // weight default 100
        this.weight = weight == null || weight < 0 ? 100 : weight;
        this.exceptionCount = new AtomicInteger(0);
    }

    public String getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * rest exception count
     */
    public void restExceptionCount() {
        exceptionCount.set(0);
    }

    /**
     * target service unreachable
     *
     * @return error service
     */
    public String exception() {
        exceptionCount.incrementAndGet();
        if (!isOnline()) {
            log.error("{} offline.", target);
            // put it to survival check list
            SurvivalChecker.add(this);
        }
        return target;
    }

    @Override
    public int hashCode() {
        return (host + "#" + target).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mapper) {
            Mapper m = (Mapper) obj;
            return target.equals(m.target) && host.equals(m.target);
        }
        return false;
    }

    /**
     * check for mapper health
     *
     * @return online or offline
     */
    public boolean isOnline() {
        return exceptionCount.get() < LoadBalanceConstant.OFFLINE_COUNT;
    }
}
