import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AuthorTabCreateComponent} from './author-tab-create.component';

describe('AuthorTabCreateComponent', () => {
  let component: AuthorTabCreateComponent;
  let fixture: ComponentFixture<AuthorTabCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorTabCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthorTabCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
