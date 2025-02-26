import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavouriteBooksComponent } from './favourite-books.component';

describe('FavouriteBooksComponent', () => {
  let component: FavouriteBooksComponent;
  let fixture: ComponentFixture<FavouriteBooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavouriteBooksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavouriteBooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
