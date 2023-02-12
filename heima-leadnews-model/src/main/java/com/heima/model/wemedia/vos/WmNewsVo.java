package com.heima.model.wemedia.vos;

import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 14:16
 */
@Data
public class WmNewsVo extends WmNews {

    /**
     * 作者名
     */
    private String authorName;
}
