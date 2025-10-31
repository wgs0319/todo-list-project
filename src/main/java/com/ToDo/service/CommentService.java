package com.ToDo.service;

import com.ToDo.domain.Comment;
import com.ToDo.domain.Task;
import com.ToDo.domain.User;
import com.ToDo.dto.CommentCreateRequest;
import com.ToDo.dto.CommentResponse;
import com.ToDo.dto.CommentUpdateRequest;
import com.ToDo.repository.CommentRepository;
import com.ToDo.repository.TaskRepository;
import com.ToDo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    // 댓글 작성
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, String authorId) {
        // Task 조회
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));

        // 작성자 조회
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // Comment 생성
        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    // Task의 모든 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getTaskComments(Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    // 댓글 상세 조회
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));
        return CommentResponse.from(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, String authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

        // 작성자 본인 확인
        if(!comment.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return CommentResponse.from(updatedComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));
        
        // 작성자 본인 확인
        if(!comment.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다");
        }

        commentRepository.deleteById(commentId);
    }

    // Task의 댓글 개수
    @Transactional(readOnly = true)
    public long getCommentCount(Long taskId) {
        return commentRepository.countByTaskId(taskId);
    }
}
