import {Component, Input} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {BookService} from '../../../shared/book-service';
import {File} from 'node:buffer';
import {NgIf} from '@angular/common';
import {fileTypeValidator} from '../../../../shared/functions/fileTypeValidator';
import {AuthorTabDataService} from '../../shared/author-tab-data.service';
import {BookResponse} from '../../../shared/models/book-response';

export const allowedFileTypes =['text/plain', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.oasis.opendocument.text'];


@Component({
  selector: 'app-chapter-edit',
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './chapter-edit.component.html',
  styleUrl: './chapter-edit.component.css'
})
export class ChapterEditComponent {

  @Input() onCloseFunction!: () => void; //? mby output?
  @Input() currentBook!: BookResponse;

  chapterUploadForm: FormGroup;

  constructor(private bookService: BookService, private nfb: FormBuilder, private authorTabDataService: AuthorTabDataService) {
    this.chapterUploadForm = this.nfb.group({
      uploadedFiles: [[], [Validators.required, fileTypeValidator(allowedFileTypes), this.fileNameValidator.bind(this)]]
    })
  }

  fileNameValidator(control: FormControl) {
    const files: File[] = control.value || [];
    if (files.length === 0) return null;
    if(files.find(file => file.name.match('^([0-9])+(\\s-\\s(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s\'_"!.-]*)?$') === null)) return null;
    return {invalidFileName: true};
  }

  uploadMessage: string  = '';
  uploadErrorMessage: string | null = null;

  onFileChange(event: any) {
    const files: FileList = event.target.files;
    if (files.length > 0) {
      this.chapterUploadForm.patchValue({
        files: Array.from(files)
      });
      this.chapterUploadForm.get('uploadedFiles')!.updateValueAndValidity();
    }
  }


  attemptChapterUpload(){
    if(this.chapterUploadForm.pristine || this.chapterUploadForm.invalid) return;
    const files: File[] = this.chapterUploadForm.value.uploadedFiles;
    if(files.length === 1){
      this.bookService.uploadChapter(this.currentBook.id, this.authorTabDataService.authorEmail, files[0]).subscribe({
        next: (_) => {
            alert('Upload successful');
          },
          error: (error: string) => {
            this.uploadErrorMessage = error;
          }
      })
    }else{
      this.bookService.uploadChaptersInBatch(this.currentBook.id, this.authorTabDataService.authorEmail, files).subscribe({
        next: (_) => {
            alert('Upload successful');
          },
          error: (error: string) => {
            this.uploadErrorMessage = error;
          }
      })
    }
  }

  resetForm(){

  }

  readonly getFilesControl = () => this.chapterUploadForm.get('uploadedFiles')! as FormControl;

}
