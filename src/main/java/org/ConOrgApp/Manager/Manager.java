package org.ConOrgApp.Manager;

abstract public class Manager {
    protected int numPages = 1;
    protected int pageSize = 0;
    protected int currPage = 0;

    public abstract void setPageSize(int size);

    public int getNumPages() {
        return numPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void nextPage() throws IndexOutOfBoundsException {
        if (currPage + 1 == numPages) {
            return;
        }
        ++currPage;
    }

    public void setCurrPage(int page) {
        if (page == numPages) {
            throw new IndexOutOfBoundsException();
        }
        this.currPage = page;
    }

    public void prevPage() throws IndexOutOfBoundsException {
        if (currPage == 0) {
            return;
        }
        --currPage;
    }

    public void menuReturn() {
        currPage = 0;
    }
}
