package com.notes.with.login.UserNotes.controller;

import com.notes.with.login.UserNotes.entity.Note;
import com.notes.with.login.UserNotes.entity.User;
import com.notes.with.login.UserNotes.repository.NoteRepository;
import com.notes.with.login.UserNotes.repository.UserRepository;
import com.notes.with.login.UserNotes.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository noteRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    public NoteController(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    private User getUser(String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        String token = header.substring(7);

        String email = jwtUtil.extractEmail(token);

        return userRepo.findByEmail(email).orElseThrow();
    }

    @GetMapping
    public List<Note> getNotes(@RequestHeader("Authorization") String header) {
        return noteRepo.findByUser(getUser(header));
    }

    @PostMapping
    public Note create(@RequestHeader("Authorization") String header,
                       @RequestBody Note note) {

        note.setUser(getUser(header));
        return noteRepo.save(note);
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable Long id,
                       @RequestHeader("Authorization") String header,
                       @RequestBody Note updated) {

        Note note = noteRepo.findById(id).orElseThrow();

        if (!note.getUser().getId().equals(getUser(header).getId())) {
            throw new RuntimeException("Unauthorized");
        }

        note.setTitle(updated.getTitle());
        note.setContent(updated.getContent());

        return noteRepo.save(note);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id,
                                      @RequestHeader("Authorization") String header) {

        Note note = noteRepo.findById(id).orElseThrow();

        if (!note.getUser().getId().equals(getUser(header).getId())) {
            throw new RuntimeException("Unauthorized");
        }

        noteRepo.delete(note);

        Map<String, String> res = new HashMap<>();
        res.put("message", "Deleted");

        return res;
    }
}
