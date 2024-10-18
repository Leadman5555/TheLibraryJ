package org.library.thelibraryj.infrastructure.error.errorTypes;

public sealed interface GeneralError permits BookError, ServiceError, UserDetailsError{
}
