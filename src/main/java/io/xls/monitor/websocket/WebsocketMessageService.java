package io.xls.monitor.websocket;

import io.xls.commons.utils.JsonResult;

/**
 * @author kevin
 * @date 2019-10-18 15:10
 */
public interface WebsocketMessageService {

    String getDestination();

    JsonResult<?> getBody();
}
