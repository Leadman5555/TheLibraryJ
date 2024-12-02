import { FilterByTagNamePipe } from './filter-by-tag-name.pipe';

describe('FilterByTagPipe', () => {
  it('create an instance', () => {
    const pipe = new FilterByTagNamePipe();
    expect(pipe).toBeTruthy();
  });
});
