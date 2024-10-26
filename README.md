Game 2D top down tiêu diệt zombie sử dụng java Swing, Graphics2D

-- Git Command --

(reset)
- git reset --soft id (quay về trạng thái staged changes của commit trước đó và có thể sửa về thêm file mà ko bị mất commit trước đó, [phù hợp để thêm file thiếu])
- git reset --mixed id (những staged changes được thêm sau commit id sẽ quay về trạng thái changes và có thể tiếp tục sửa và thêm file, [phù hợp để sửa file])
- git rest --hard id (quay về commit id và những commit và code của commit được thêm sau commit id sẽ bị xoá)

git log --oneline (xem lịch sử commit)
git merge --abort (hủy bỏ quá trình hợp nhất)

Lưu ý conflict message:
- current changes (thay đổi hiện tại)
- incoming changes ( những thay đổi mới)

(hợp nhất)
- git merge branchName (hợp nhất code với nhánh khác theo thứ tự, cái nào commit trước sẽ được xếp trước)
+ xử lý conflict nếu có:
  + sau khi fix conflict:
    + git add . hoặc fileName
    + git commit -m "text"
    + git push origin branchName

+ git push 

- git rebase branchName (hợp nhất code với nhánh khác không theo thứ tự, những commit của nhánh kia sẽ được ưu tiên lên trước rồi mới tới commit của mình)
+ xử lý conflict nếu có:
  + sau khi fix conflict:
    + git add . hoặc fileName
    + git rebase --continue
    + git push --force




