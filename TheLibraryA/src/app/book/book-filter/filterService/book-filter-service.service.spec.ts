import { TestBed } from '@angular/core/testing';

import { BookFilterService } from './book-filter.service';

describe('BookFilterServiceService', () => {
  let service: BookFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookFilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
