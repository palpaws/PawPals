document.addEventListener('DOMContentLoaded', function() {
    const bell = document.getElementById('notificationBell');
    const dropdown = document.getElementById('notificationDropdown');
    const badge = document.getElementById('notificationBadge');
    const list = document.getElementById('notificationList');
    const markAllBtn = document.getElementById('markAllRead');

    if (!bell) return; // not logged in

    let isOpen = false;

    // Auto-generate notifications from reminders on page load
    generateNotificationsFromReminders();

    // Load unread count on page load
    fetchUnreadCount();

    function generateNotificationsFromReminders() {
        fetch('/api/notifications/generate', { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success && data.unreadCount > 0) {
                    updateBadgeCount(data.unreadCount);
                }
            })
            .catch(() => {});
    }

    // Toggle dropdown
    bell.addEventListener('click', function(e) {
        e.stopPropagation();
        isOpen = !isOpen;
        if (isOpen) {
            dropdown.classList.add('show');
            loadNotifications();
        } else {
            dropdown.classList.remove('show');
        }
    });

    // Close dropdown on outside click
    document.addEventListener('click', function(e) {
        if (!document.querySelector('.notification-bell-wrapper').contains(e.target)) {
            dropdown.classList.remove('show');
            isOpen = false;
        }
    });

    // Mark all as read
    if (markAllBtn) {
        markAllBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            fetch('/api/notifications/read-all', { method: 'PUT' })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        // Update all items to read
                        document.querySelectorAll('.notification-item.unread').forEach(item => {
                            item.classList.remove('unread');
                            item.classList.add('read');
                        });
                        badge.style.display = 'none';
                        badge.textContent = '0';
                    }
                });
        });
    }

    function loadNotifications() {
        fetch('/api/notifications')
            .then(res => res.json())
            .then(notifications => {
                renderNotifications(notifications);
                updateBadge(notifications);
            });
    }

    function renderNotifications(notifications) {
        if (!notifications || notifications.length === 0) {
            list.innerHTML = '<div class="dropdown-empty">Không có thông báo</div>';
            return;
        }

        let html = '';
        notifications.forEach(n => {
            const isUnread = !n.isRead;
            html += `
                <div class="notification-item ${isUnread ? 'unread' : 'read'}" data-id="${n.notificationId}" data-read="${n.isRead}">
                    <div class="notification-item-title">${escapeHtml(n.title)}</div>
                    <div class="notification-item-content">${escapeHtml(n.content)}</div>
                    <div class="notification-item-time">${n.timeAgo || ''}</div>
                </div>
            `;
        });
        list.innerHTML = html;

        // Click to mark as read
        document.querySelectorAll('.notification-item.unread').forEach(item => {
            item.addEventListener('click', function() {
                const id = this.dataset.id;
                markAsRead(id, this);
            });
        });
    }

    function markAsRead(id, element) {
        fetch('/api/notifications/' + id + '/read', { method: 'PUT' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    element.classList.remove('unread');
                    element.classList.add('read');
                    element.dataset.read = 'true';
                    // Update badge
                    const unreadItems = document.querySelectorAll('.notification-item.unread');
                    const count = unreadItems.length;
                    updateBadgeCount(count);
                }
            });
    }

    function updateBadge(notifications) {
        const unreadCount = notifications.filter(n => !n.isRead).length;
        updateBadgeCount(unreadCount);
    }

    function updateBadgeCount(count) {
        if (count > 0) {
            badge.style.display = 'flex';
            badge.textContent = count > 99 ? '99+' : count;
        } else {
            badge.style.display = 'none';
            badge.textContent = '0';
        }
    }

    function fetchUnreadCount() {
        fetch('/api/notifications/unread-count')
            .then(res => res.json())
            .then(data => {
                updateBadgeCount(data.count);
            });
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Periodically refresh unread count every 60 seconds
    setInterval(fetchUnreadCount, 60000);
});