package com.example.newhisolve.Model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class TestCase {

    private String input;
    private String expectedOutput;
    private boolean isHidden;
}
