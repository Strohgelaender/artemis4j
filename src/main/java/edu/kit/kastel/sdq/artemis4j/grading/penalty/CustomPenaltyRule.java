/* Licensed under EPL-2.0 2024. */
package edu.kit.kastel.sdq.artemis4j.grading.penalty;

import java.util.List;

import edu.kit.kastel.sdq.artemis4j.grading.Annotation;

public final class CustomPenaltyRule implements PenaltyRule {
    @Override
    public Points calculatePoints(List<Annotation> annotations) {
        return new Points(
                annotations.stream()
                        .mapToDouble(annotation -> annotation.getCustomScore().orElseThrow())
                        .sum(),
                false);
    }
}
