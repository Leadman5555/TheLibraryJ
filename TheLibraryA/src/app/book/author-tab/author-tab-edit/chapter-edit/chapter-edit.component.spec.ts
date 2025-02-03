import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChapterEditComponent } from './chapter-edit.component';

describe('ChapterEditComponent', () => {
  let component: ChapterEditComponent;
  let fixture: ComponentFixture<ChapterEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChapterEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChapterEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
