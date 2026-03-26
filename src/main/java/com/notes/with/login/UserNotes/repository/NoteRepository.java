package com.notes.with.login.UserNotes.repository;

import com.notes.with.login.UserNotes.entity.Note;
import com.notes.with.login.UserNotes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);
}
