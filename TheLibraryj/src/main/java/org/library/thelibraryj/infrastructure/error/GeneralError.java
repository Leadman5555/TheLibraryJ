package org.library.thelibraryj.infrastructure.error;

public sealed interface GeneralError permits BookError, ServiceError, UserDetailsError{
}
