package io.xls.monitor.domain.es;

import io.xls.core.data.elasticsearch.domain.AbstractUUIDPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 商户
 *
 * @author kevin
 * @date 2019-10-17 17:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Document(indexName = MchtInfo.INDEX_NAME, type = "mchtInfo")
public class MchtInfo extends AbstractUUIDPersistable {

    public static final String INDEX_NAME = "monitor-mcht";

    /**
     * 商户名称
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * 所在省
     */
    @Field(type = FieldType.Keyword)
    private String province;

    /**
     * 入驻时间
     */
    @Field(type = FieldType.Date)
    private String crateDate;

    /**
     * <pre>
     * 数据来源
     *      kys:卡医生
     *      xls:新零售
     *      test: 测试环境
     * </pre>
     */
    @Field(type = FieldType.Keyword)
    private String source;
}
