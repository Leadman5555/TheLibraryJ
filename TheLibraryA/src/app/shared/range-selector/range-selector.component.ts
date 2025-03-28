import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {NgClass, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-range-selector',
  imports: [NgClass, NgOptimizedImage],
  templateUrl: './range-selector.component.html',
  styleUrl: './range-selector.component.css',
  standalone: true
})
export class RangeSelectorComponent {
  @Input() parentControl!: FormControl;
  @Input() values!: any[];
  @Input() selectedIndex: number = 0;

  selectValue(index: number): void {
    this.selectedIndex = index;
    this.parentControl.markAsDirty();
    this.parentControl.patchValue(index);
  }
}
