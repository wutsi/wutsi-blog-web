function WutsiEJS (holderId, publishCallback){
    this.holderId = holderId;
    this.storyId = '0';
    this.dirty = false;
    this.saving = false;
    this.editorjs = null;

    this.config = {
        autosave: 60000,
        saveUrl: '/me/story/editor/save',
        selectors: {
            title: '#title',
            btnPreview: '#btn-preview',
            btnPublish: '#btn-publish',
            btnClose: '#btn-close',
            storyStatus: '#story-status',
            editorStatus: '#editor-status'
        },
        labels: {
            draft: 'Brouillon',
            published: 'Publié',
            saving: 'Enregistrement en cours...',
            saved: 'Enregistré',
            modified: 'Modifié',
            publish: 'Publiez',
            saveAndPublish: 'Enregistrez et Publiez'
        }
    };


    this.setup = function(story) {
        console.log('Initializing Editor');

        const me = this;

        this.init_editorjs(story);
        this.init_toolbar(story);
        this.init_autosave(story);

        $(this.config.selectors.title).on('keydown', function(){ me.set_dirty(true)} );

        window.addEventListener('beforeunload', function(){
            if (!story || story.draft) {
                /* Save on unload only fro draft stories */
                console.log('Unloading the window');
                me.editorjs_save();
            }
        });
    };


    this.editorjs_save = function(callback) {
        console.log('editorjs_save()');

        this.saving = true;
        if (this.dirty){
            console.log("Document is dirty.Saving...");

            const me = this;
            this.show_status('#story-alert-saving', true);
            this.editorjs
                .save()
                .then(function(data){
                    console.log('Saved', data);
                    me.set_dirty(false);

                    const request = {
                        id: me.storyId,
                        title: $(me.config.selectors.title).val(),
                        content: JSON.stringify(data)
                    };

                    wutsi.httpPost(me.config.saveUrl, request, true)
                        .then(function(story){
                            me.storyId = story.id;
                            if (callback){
                                callback();
                            }
                        })
                        .catch(function(){
                            me.set_dirty(true);
                        })
                        .finally(function(){
                            me.saving = false;
                            me.update_toolbar();
                        });
                })
                .catch(function(error){
                    console.error("Save Error", error);
                });
        } else {
            console.log("Document not dirty. Nothing to save");
            if (callback){
                callback();
            }
        }
    };

    this.init_autosave = function(story) {
        if (!story || story.draft) {
            console.log('The Story in DRAFT. Enabling auto-save');
            const me = this;
            setInterval(function () {
                me.editorjs_save()
            }, this.config.autosave);
        } else {
            console.log('The Story in PUBLISHED. Disabling auto-save');
        }
    };

    this.init_editorjs = function(story) {
        console.log('Initializing EditorJS');

        const me = this;
        const tools = {
            header: {
                class: Header,
                config: {
                    levels: [2, 3, 4],
                    defaultLevel: 2
                }
            },
            image: SimpleImage,
            quote: Quote,
            delimiter: Delimiter,
            code: CodeTool,

            list: {
                class: List,
                inlineToolbar: true
            },
            marker: Marker

            /*
             * We are disabling for the moment because of bug that prevent to consume uploaded images
             *
            image: {
                class: ImageTool,
                config:{
                    endpoints: {
                        byFile: '/upload?'
                    }
                }
            }
             */
        };
        if (story) {
            console.log('Initializing editor with Story#' + story.id, JSON.parse(story.content));
            this.storyId = story.id;
            $(this.config.selectors.title).val(story.title);
            this.editorjs = new EditorJS({
                holderId: this.holderId,
                autofocus: true,
                tools: tools,
                data: JSON.parse(story.content),
                onChange: function () {
                    me.set_dirty(true);
                }
            });

        } else {
            console.log('Initializing empty editor');

            this.editorjs = new EditorJS({
                holderId: me.holderId,
                autofocus: true,
                tools: tools,
                onChange: function () {
                    me.set_dirty(true);
                }
            });
        }

        this.set_dirty(false);
    };

    this.init_toolbar = function(story) {
        const me = this;

        // Story Status
        if (!story || story.draft) {
            $(this.config.selectors.storyStatus).text(this.config.labels.draft);
            $(this.config.selectors.storyStatus).addClass('status-draft');
        } else {
            $(this.config.selectors.storyStatus).text(this.config.labels.published);
            $(this.config.selectors.storyStatus).addClass('status-published');
        }

        // Editor Status
        $(this.config.selectors.editorStatus).text('');

        // Preview
        $(this.config.selectors.btnPreview).on('click', function () {
            if (me.storyId > 0) {
                window.open('/read/' + me.storyId + '?preview=true')
            }
        });

        // Publish button
        $(this.config.selectors.btnPublish).text( !story || story.draft ? this.config.labels.publish : this.config.labels.saveAndPublish);
        $(this.config.selectors.btnPublish).on('click', function () {
            me.editorjs_save(function() {
                publishCallback(me.storyId);
            });
        });

        // Close button
        $(this.config.selectors.btnClose).on('click', function () {
            if (!story || story.draft) {
                window.location.href = '/me/story/draft';
            } else {
                window.location.href = '/me/story/published';
            }
        });


        this.update_toolbar();
    };

    this.update_toolbar = function () {
        // Status
        if (this.saving) {
            $(this.config.selectors.editorStatus).text(this.config.labels.saving);
        } else {
            if (this.dirty) {
                $(this.config.selectors.editorStatus).text(this.config.labels.modified);
            } else {
                $(this.config.selectors.editorStatus).text(this.config.labels.saved);
            }
        }

        // Preview
        if (this.storyId > 0){
            $(this.config.selectors.btnPreview).removeAttr('disabled');
        }
    };

    this.set_dirty = function(value) {
        console.log('set_dirty', value);
        this.dirty = value;
        if (this.dirty){
            this.update_toolbar();
        }
    };

    this.show_status = function(selector, show){
        if (show){
            $(selector).css('display', 'inline-block');
        } else {
            $(selector).css('display', 'none');
        }
    };
}
