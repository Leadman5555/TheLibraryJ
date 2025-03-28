import {TestBed} from '@angular/core/testing';

import {AuthorTabDataService} from './author-tab-data.service';

describe('AuthorTabDataService', () => {
  let service: AuthorTabDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthorTabDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
