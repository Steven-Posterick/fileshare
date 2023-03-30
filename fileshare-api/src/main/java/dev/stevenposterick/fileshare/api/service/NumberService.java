package dev.stevenposterick.fileshare.api.service;

import org.springframework.stereotype.Service;

@Service
public class NumberService {

    public Integer tryParse(String numberString){
        try {
            return Integer.parseInt(numberString);
        } catch (Exception exception){
            return null;
        }
    }
}
