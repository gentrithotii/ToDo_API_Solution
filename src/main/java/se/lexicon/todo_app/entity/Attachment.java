package se.lexicon.todo_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor

@ToString(exclude = "todo")
@EqualsAndHashCode(exclude = "todo")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data; // Store the file content

    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public Attachment(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;

        // Sync the other side if not already present
        if (todo != null) {
            todo.getAttachments().add(this);
        }
    }

}
