package edu.kit.kastel.sdq.artemis4j.grading;

import edu.kit.kastel.sdq.artemis4j.ArtemisNetworkException;
import edu.kit.kastel.sdq.artemis4j.LazyNetworkValue;
import edu.kit.kastel.sdq.artemis4j.client.CourseDTO;
import edu.kit.kastel.sdq.artemis4j.client.ExamDTO;
import edu.kit.kastel.sdq.artemis4j.client.ProgrammingExerciseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * A course, containing exercises.
 */
public class Course extends ArtemisConnectionHolder {
    private final CourseDTO dto;
    private final LazyNetworkValue<List<Exercise>> exercises;
    private final LazyNetworkValue<List<Exam>> exams;

    public Course(CourseDTO dto, ArtemisConnection connection) {
        super(connection);

        this.dto = dto;
        this.exercises = new LazyNetworkValue<>(() -> {
            List<Exercise> result = new ArrayList<>();

            result.addAll(ProgrammingExerciseDTO.fetchAll(connection.getClient(), dto.id())
                .stream()
                .map(exerciseDTO -> new ProgrammingExercise(exerciseDTO, this))
                .toList());

            // TODO: add text exercises

            return result;

        });
        this.exams = new LazyNetworkValue<>(() -> ExamDTO.fetchAll(connection.getClient(), dto.id()).stream().map(examDTO -> new Exam(examDTO, this)).toList());
    }

    /**
     * Checks if the given user is an instructor of this course.
     *
     * @param user the user to check
     * @return true if the user is an instructor, false otherwise
     */
    public boolean isInstructor(User user) {
        return user != null && user.getGroups().contains(this.dto.instructorGroup());
    }

    public int getId() {
        return this.dto.id();
    }

    public String getTitle() {
        return this.dto.title();
    }

    /**
     * Gets all programming exercises of this course. The result is fetched lazily and then cached.
     *
     * @return
     * @throws ArtemisNetworkException
     */
    public List<ProgrammingExercise> getProgrammingExercises() throws ArtemisNetworkException {
        return this.exercises.get()
            .stream()
            .filter(ProgrammingExercise.class::isInstance)
            .map(ProgrammingExercise.class::cast)
            .toList();
    }

    /**
     * Gets all exams of this course. The result is fetched lazily and then cached.
     *
     * @return
     * @throws ArtemisNetworkException
     */
    public List<Exam> getExams() throws ArtemisNetworkException {
        return this.exams.get();
    }
}
