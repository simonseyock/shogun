package de.terrestris.shogun.lib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebSocketMessage<DataType extends Serializable> {

    public WebSocketMessage(String message) {
        this.message = message;
    }

    private String message;

    private DataType data;

}
