import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorTabComponent } from './author-tab.component';

describe('AuthorTabComponent', () => {
  let component: AuthorTabComponent;
  let fixture: ComponentFixture<AuthorTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorTabComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthorTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
