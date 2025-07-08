package com.hust.ewsystem.DAO.VO;

import com.hust.ewsystem.DAO.PO.CommonData;
import lombok.Data;

import java.util.List;

@Data
public class Calculate {
    private List<CommonData> value;

    private String picName;
}
