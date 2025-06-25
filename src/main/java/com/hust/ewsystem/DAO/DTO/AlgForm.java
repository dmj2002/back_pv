package com.hust.ewsystem.DAO.DTO;


import com.hust.ewsystem.DAO.PO.Algorithms;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlgForm {

    private Algorithms algorithms;

    private MultipartFile train;        // 训练py

    private MultipartFile predict;        // 预测py

}
