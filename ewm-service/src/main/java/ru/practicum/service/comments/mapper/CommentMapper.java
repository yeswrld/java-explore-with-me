package ru.practicum.service.comments.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.service.comments.dto.CommentDto;
import ru.practicum.service.comments.dto.FullCommentDto;
import ru.practicum.service.comments.dto.NewCommentDto;
import ru.practicum.service.comments.model.Comment;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.State;
import ru.practicum.service.user.dto.UserShortDto;
import ru.practicum.service.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, UserShortDto userShortDto) {
        return new CommentDto(comment.getId(), comment.getText(), userShortDto, comment.getCreated(), comment.getUpdated());
    }

    public static Comment toComment(NewCommentDto newCommentDto, Event event, User user) {
        return new Comment(null, newCommentDto.getText(), event, user, LocalDateTime.now(), LocalDateTime.now(), State.PENDING);
    }

    public static FullCommentDto toFullComment(Comment comment, UserShortDto userShortDto) {
        return new FullCommentDto(null, comment.getText(), userShortDto, comment.getCreated(), comment.getUpdated(), comment.getState());
    }
}
