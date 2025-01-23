import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RangeSelectorComponent } from './range-selector.component';

describe('RangeSelectorComponent', () => {
  let component: RangeSelectorComponent;
  let fixture: ComponentFixture<RangeSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RangeSelectorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RangeSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
