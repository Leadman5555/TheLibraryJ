import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BookPreviewCardComponent} from './book-preview-card.component';

describe('BookPreviewCardComponent', () => {
  let component: BookPreviewCardComponent;
  let fixture: ComponentFixture<BookPreviewCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookPreviewCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookPreviewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
