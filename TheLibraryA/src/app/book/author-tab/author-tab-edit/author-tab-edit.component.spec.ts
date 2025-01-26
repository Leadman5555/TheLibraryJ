import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorTabEditComponent } from './author-tab-edit.component';

describe('AuthorTabEditComponent', () => {
  let component: AuthorTabEditComponent;
  let fixture: ComponentFixture<AuthorTabEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorTabEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthorTabEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
