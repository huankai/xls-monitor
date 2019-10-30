package io.xls.monitor.domain.es;

import io.xls.core.data.elasticsearch.domain.AbstractUUIDPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

/**
 * 交易
 *
 * @author kevin
 * @date 2019-10-17 17:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Document(indexName = TransactionInformation.INDEX_NAME, type = "transactionInformation")
public class TransactionInformation extends AbstractUUIDPersistable {

    public static final String INDEX_NAME = "monitor-transaction";

    /**
     * 商户id
     */
    @Field(type = FieldType.Keyword)
    private String mchtId;

    /**
     * 交易时间
     */
    @Field(type = FieldType.Date)
    private String createDate;

    /**
     * 交易发生地:所在省
     */
    @Field(type = FieldType.Keyword)
    private String province;

    /**
     * 交易用户Id
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * 交易用户名
     */
    @Field(type = FieldType.Keyword)
    private String userName;

    /**
     * 交易金额
     */
    @Field(type = FieldType.Double)
    private BigDecimal amount;

    /**
     * <pre>
     *
     * 交易通道：
     * 乐刷
     * 中付
     * 通联
     * 合利宝
     * 汇卡
     * 支付宝
     * 微信
     * </pre>
     */
    @Field(type = FieldType.Keyword)
    private String passageway;

    /**
     * <pre>
     * 交易设备:
     * 扫码设备
     * POS机
     * 刷卡
     * </pre>
     */
    @Field(type = FieldType.Keyword)
    private String equipment;

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
