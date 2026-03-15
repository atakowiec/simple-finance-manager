package pl.pollub.backend.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.backend.auth.user.User;

import java.util.List;

/**
 * Default implementation of {@link ActivityLogService}.
 * Delegates persistence to {@link ActivityLogRepository}.
 */
@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    public ActivityLog save(ActivityLog log) {
        return activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getLogsForGroup(Long groupId) {
        return activityLogRepository.findAllByGroupIdOrderByTimestampDesc(groupId);
    }

    @Override
    public List<ActivityLog> getLogsForUser(User user) {
        return activityLogRepository.findAllByUser_IdOrderByTimestampDesc(user.getId());
    }
}

