package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import vn.localhelp.core.domain.entity.Notifications;
import vn.localhelp.core.domain.response.notification.NotificationResponse;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  NotificationResponse toResponse(Notifications notification);
}
