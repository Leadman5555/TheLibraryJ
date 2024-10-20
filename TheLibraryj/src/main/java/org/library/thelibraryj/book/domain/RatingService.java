//package org.library.thelibraryj.book.domain;
//
//import org.library.thelibraryj.userInfo.UserInfoService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@Transactional(readOnly = true)
//class RatingService {
//    private final RatingRepository ratingRepository;
//    private final BookMapper mapper;
//    private final UserInfoService userInfoService;
//
//    public RatingService(RatingRepository ratingRepository, BookMapper mapper, UserInfoService userInfoService) {
//        this.ratingRepository = ratingRepository;
//        this.mapper = mapper;
//        this.userInfoService = userInfoService;
//    }
//
//    public List<Rating> getAllRatingsForBook(UUID bookId) {
//        return ratingRepository.getAllRatingsForBook(bookId);
//    }
//}
