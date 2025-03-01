import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PavilionOfGloryComponent } from './pavilion-of-glory.component';

describe('PavilionOfGloryComponent', () => {
  let component: PavilionOfGloryComponent;
  let fixture: ComponentFixture<PavilionOfGloryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PavilionOfGloryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PavilionOfGloryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
