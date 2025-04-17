package com.hust.ewsystem.DAO.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.util.List;

@Data
public class ModelChangeDTO {

    private List<Integer> modelIds;

    private String modelName;

    private Integer alertWindowSize;

    private Integer alertInterval;

    private JsonNode params;
}
