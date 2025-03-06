import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookSubscriptionsComponent } from './book-subscriptions.component';

describe('BookSubscriptionsComponent', () => {
  let component: BookSubscriptionsComponent;
  let fixture: ComponentFixture<BookSubscriptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookSubscriptionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookSubscriptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
