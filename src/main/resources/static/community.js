document.addEventListener('DOMContentLoaded', function() {
    // Fetch and render comments via JS on initial load to support recursive nesting
    if (typeof postId !== 'undefined' && postId) {
        fetch('/community/post/' + postId + '/comments')
            .then(r => r.json())
            .then(data => {
                if (data.success) {
                    renderAllComments(data.comments);
                }
            });
    }

    var likeBtn = document.getElementById('likeBtn');
    if (likeBtn) {
        likeBtn.addEventListener('click', function() {
            toggleLike(postId);
        });
    }

    document.addEventListener('click', function(e) {
        var target = e.target.closest('.reply-toggle-btn');
        if (target) {
            e.preventDefault();
            var commentId = target.getAttribute('data-comment-id');
            toggleReplyForm(commentId);
        }
    });

    document.addEventListener('click', function(e) {
        var target = e.target.closest('.reply-submit-btn');
        if (target) {
            e.preventDefault();
            var commentId = target.getAttribute('data-comment-id');
            submitReply(commentId);
        }
    });

    document.addEventListener('click', function(e) {
        var target = e.target.closest('.show-replies-btn');
        if (target) {
            e.preventDefault();
            var commentId = target.getAttribute('data-comment-id');
            toggleReplies(commentId, target);
        }
    });
});

function toggleLike(postId) {
    fetch('/community/post/' + postId + '/like', { method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'} })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            var btn = document.getElementById('likeBtn');
            document.getElementById('likeCount').textContent = data.likeCount;
            if (data.liked) {
                btn.classList.remove('btn-outline-danger');
                btn.classList.add('btn-danger');
                btn.querySelector('i').className = 'bi bi-heart-fill';
            } else {
                btn.classList.remove('btn-danger');
                btn.classList.add('btn-outline-danger');
                btn.querySelector('i').className = 'bi bi-heart';
            }
        } else {
            alert(data.message);
        }
    });
}

function submitComment(postId) {
    var content = document.getElementById('commentContent').value.trim();
    if (!content) { alert('Please enter a comment'); return; }

    fetch('/community/post/' + postId + '/comment', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'content=' + encodeURIComponent(content)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            document.getElementById('commentContent').value = '';
            renderAllComments(data.comments);
        } else {
            alert(data.message);
        }
    });
}

function toggleReplyForm(commentId) {
    var form = document.getElementById('replyForm-' + commentId);
    if (form) {
        form.style.display = (form.style.display === 'none' || form.style.display === '') ? 'block' : 'none';
    }
}

function toggleReplies(commentId, btn) {
    var wrapper = document.getElementById('replies-' + commentId);
    if (wrapper) {
        if (wrapper.style.display === 'none' || wrapper.style.display === '') {
            wrapper.style.display = 'block';
            btn.innerHTML = '<i class="bi bi-caret-up-fill" style="font-size: 0.7rem;"></i> Ẩn câu trả lời';
        } else {
            wrapper.style.display = 'none';
            var replyCount = wrapper.querySelectorAll('.comment-item').length;
            btn.innerHTML = '<i class="bi bi-caret-down-fill" style="font-size: 0.7rem;"></i> Xem ' + replyCount + ' câu trả lời';
        }
    }
}

function submitReply(commentId) {
    var replyInput = document.querySelector('.reply-input[data-comment-id="' + commentId + '"]');
    if (!replyInput) return;
    var content = replyInput.value.trim();
    if (!content) { alert('Please enter a reply'); return; }

    fetch('/community/comment/' + commentId + '/reply', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'content=' + encodeURIComponent(content)
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            replyInput.value = '';
            var form = document.getElementById('replyForm-' + commentId);
            if (form) form.style.display = 'none';
            if (data.comments) {
                renderAllComments(data.comments);
            }
        } else {
            alert(data.message);
        }
    });
}

function renderAllComments(comments) {
    var container = document.getElementById('commentsSection');
    var commentCountSpan = document.getElementById('commentCount');

    var html = '<h5><i class="bi bi-chat-square-text"></i> Comments</h5>';

    if (comments.length === 0) {
        html += '<div class="text-muted text-center py-3">No comments yet. Be the first to comment!</div>';
    } else {
        // Root comments pass isRoot=true
        comments.forEach(function(c) {
            html += renderCommentHtml(c, true);
        });
    }

    container.innerHTML = html;
    if (commentCountSpan) {
        commentCountSpan.textContent = comments.length;
    }
}

function renderCommentHtml(comment, isRoot) {
    var html = '<div class="comment-item mb-3" data-comment-id="' + comment.commentId + '">';
    html += '<div class="d-flex align-items-start">';
    html += '<div class="avatar-circle-sm me-2"><span>' + escapeHtml(comment.authorName.charAt(0)) + '</span></div>';
    html += '<div class="flex-grow-1">';
    html += '<div class="comment-header">';
    html += '<strong>' + escapeHtml(comment.authorName) + '</strong>';
    html += '<small class="text-muted ms-2">' + formatDate(comment.createdAt) + '</small>';
    html += '</div>';
    html += '<p class="mb-1">' + escapeHtml(comment.content) + '</p>';
    html += '<button class="btn btn-sm btn-link p-0 reply-toggle-btn" data-comment-id="' + comment.commentId + '" style="font-size: 0.85rem; color: #65676b; text-decoration: none;">';
    html += '<i class="bi bi-reply"></i> Reply</button>';
    html += '<div class="reply-form mt-2" style="display:none;" id="replyForm-' + comment.commentId + '">';
    html += '<div class="input-group input-group-sm">';
    html += '<input type="text" class="form-control reply-input" placeholder="Write a reply..." data-comment-id="' + comment.commentId + '">';
    html += '<button class="btn btn-primary btn-sm reply-submit-btn" data-comment-id="' + comment.commentId + '">Reply</button>';
    html += '</div></div></div></div>';

    // Only root comments get the show/hide toggle for replies
    if (isRoot && comment.replies && comment.replies.length > 0) {
        var totalReplies = countAllReplies(comment);
        html += '<div class="mt-2 ms-4">';
        html += '<button class="btn btn-sm btn-link p-0 show-replies-btn" data-comment-id="' + comment.commentId + '" style="color: #216fdb; font-weight: 600; text-decoration: none;">';
        html += '<i class="bi bi-caret-down-fill" style="font-size: 0.7rem;"></i>';
        html += ' Xem ' + totalReplies + ' câu trả lời';
        html += '</button>';
        html += '<div class="replies-wrapper" style="display:none;" id="replies-' + comment.commentId + '">';
        html += '<div class="replies ms-3 border-start ps-3">';
        comment.replies.forEach(function(r) {
            html += renderCommentHtml(r, false); // replies are not root
        });
        html += '</div></div></div>';
    }

    // Non-root comments: show their replies directly without toggle
    if (!isRoot && comment.replies && comment.replies.length > 0) {
        html += '<div class="mt-2 ms-4">';
        html += '<div class="replies ms-3 border-start ps-3">';
        comment.replies.forEach(function(r) {
            html += renderCommentHtml(r, false);
        });
        html += '</div></div>';
    }

    html += '</div>';
    return html;
}

function countAllReplies(comment) {
    var count = 0;
    if (comment.replies) {
        comment.replies.forEach(function(r) {
            count += 1 + countAllReplies(r);
        });
    }
    return count;
}

function formatDate(dateStr) {
    var d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}