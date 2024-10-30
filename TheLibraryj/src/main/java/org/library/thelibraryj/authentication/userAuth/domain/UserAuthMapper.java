package org.library.thelibraryj.authentication.userAuth.domain;

import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserAuthMapper {
    UserAuth userAuthRequestToUserAuth(UserAuth userAuth);
    @Mapping(source = "uuid", target = "id")
    UserAuthResponse userAuthToUserAuthResponse(UserAuth userAuth);
}
