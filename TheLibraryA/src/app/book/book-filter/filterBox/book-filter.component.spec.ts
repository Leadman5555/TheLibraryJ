import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BookFilterComponent} from './book-filter.component';

describe('BookFilterComponent', () => {
  let component: BookFilterComponent;
  let fixture: ComponentFixture<BookFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookFilterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
