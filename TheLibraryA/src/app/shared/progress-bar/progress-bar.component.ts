import {Component, Input} from '@angular/core';
import { NgStyle } from '@angular/common';


@Component({
  selector: 'app-progress-bar',
  imports: [NgStyle],
  standalone: true,
  templateUrl: './progress-bar.component.html',
  styleUrl: './progress-bar.component.css'
})
export class ProgressBarComponent {

  @Input() current: number = 0;
  @Input() goal: number = 100;

  getProgressPercentage() {
    if(this.current >= this.goal) return 100;
    return (this.current / this.goal) * 100;
  }
}
