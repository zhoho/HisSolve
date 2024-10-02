package com.example.newhisolve.Model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class GradingTestCase {

    private String input;
    private String expectedOutput;
}
