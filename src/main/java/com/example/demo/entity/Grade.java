package com.example.demo.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * @description grade
 * @author gzy
 * @date 2023-01-12
 */
@Data
public class Grade implements Serializable {

    private static final long serialVersionUID = 42451233741564246L;

    @TableId(type = IdType.AUTO)
    /**
     * 关键词
     */
    private String str;

    /**
     * 词频计数
     */
    private Integer count;

    /**
     * 评分
     */
    private Integer grade;

    public Grade() {}
}