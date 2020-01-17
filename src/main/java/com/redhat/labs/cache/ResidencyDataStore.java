package com.redhat.labs.cache;

import java.util.List;
import java.util.Map;

/**
 * This interface defines the contract to store data for the received events.
 *
 * @author faisalmasood
 */
public interface ResidencyDataStore {

    /**
     * throws {@link RuntimeException} if the store call is not successfull
     *
     * there are two entries in the values map
     * residency.yaml - cluster yaml
     * metadata.json - info about residency
     * @param key
     * @param residencyInformation
     */
    public void store(String key, ResidencyInformation residencyInformation);

    /**
     * return NULL if the key is not present
     * there are two entries in the values map
     *
     * throws {@link RuntimeException} if the store call is not successfull
     * @param key
     * @return
     */
    public ResidencyInformation fetch(String key);

    /**
     * return all the KEYS stored in the cahe.
     * @return
     */
    public List<String> getAllKeys();

}
