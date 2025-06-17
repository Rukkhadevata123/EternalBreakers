class Game {
    static CONSTANTS = {
        SCREEN_WIDTH: 600,
        SCREEN_HEIGHT: 400,
        BALL_SIZE_RADIUS: 10,
        PADDLE_HEIGHT_CONSTANT: 10,
        PADDLE_START_Y: 350,
        PADDLE_LENGTH_LONG: 160,
        PADDLE_LENGTH_NORMAL: 80,
        PADDLE_LENGTH_SHORT: 40,
        TOTAL_BRICK_ROWS: 5,
        TOTAL_BRICK_COLUMNS: 10,
        BRICK_TILE_WIDTH: 60,
        BRICK_TILE_HEIGHT: 15,
        BRICK_TILE_GAP: 4,
        BALL_SPEED_SLOW: 8,
        BALL_SPEED_NORMAL: 9,
        BALL_SPEED_FAST: 10,
        BALL_SPEED_HELL: 15
    };

    static BRICK_COLORS = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7'];
    static BRICK_BORDER_COLORS = ['#E55A5A', '#3CBAB3', '#3A9BC1', '#7FB69E', '#F0D785'];

    constructor() {
        this.canvas = document.getElementById('gameCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.scoreDisplay = document.getElementById('scoreDisplay');
        this.speedDisplay = document.getElementById('speedDisplay');
        this.paddleDisplay = document.getElementById('paddleDisplay');
        this.statusMessage = document.getElementById('statusMessage');

        // 游戏状态
        this.score = 0;
        this.ballVelocityX = Game.CONSTANTS.BALL_SPEED_NORMAL;
        this.ballVelocityY = Game.CONSTANTS.BALL_SPEED_NORMAL;
        this.ballAngle = Math.PI / 4;
        this.gameRunning = false;
        this.hellMode = false;
        this.randomMode = false;
        this.ballX = Game.CONSTANTS.SCREEN_WIDTH / 2;
        this.ballY = Game.CONSTANTS.SCREEN_HEIGHT / 2;
        this.paddleX = Game.CONSTANTS.SCREEN_WIDTH / 2 - Game.CONSTANTS.PADDLE_LENGTH_NORMAL / 2;
        this.paddleWidth = Game.CONSTANTS.PADDLE_LENGTH_NORMAL;
        this.paddleVisible = true;
        this.bricks = [];
        this.countdown = null;
        this.messageHider = null;

        this.initBricks();
        this.setupEventListeners();
    }

    initBricks() {
        this.bricks = [];
        const C = Game.CONSTANTS;
        for (let i = 0; i < C.TOTAL_BRICK_ROWS; i++) {
            this.bricks[i] = [];
            for (let j = 0; j < C.TOTAL_BRICK_COLUMNS; j++) {
                this.bricks[i][j] = {
                    visible: true,
                    x: j * C.BRICK_TILE_WIDTH + C.BRICK_TILE_GAP,
                    y: i * C.BRICK_TILE_HEIGHT + C.BRICK_TILE_GAP + 30,
                    width: C.BRICK_TILE_WIDTH - 2 * C.BRICK_TILE_GAP,
                    height: C.BRICK_TILE_HEIGHT - 2 * C.BRICK_TILE_GAP,
                    color: Game.BRICK_COLORS[i]
                };
            }
        }
    }

    setupEventListeners() {
        // 鼠标控制
        this.canvas.addEventListener('mousemove', (e) => {
            const rect = this.canvas.getBoundingClientRect();
            const mouseX = e.clientX - rect.left;
            this.paddleX = Math.max(0, Math.min(Game.CONSTANTS.SCREEN_WIDTH - this.paddleWidth, mouseX - this.paddleWidth / 2));
        });

        // 使用数组简化重复的按钮事件绑定
        const buttonConfigs = [
            ['slowSpeedBtn', () => this.setBallSpeed(Game.CONSTANTS.BALL_SPEED_SLOW)],
            ['normalSpeedBtn', () => this.setBallSpeed(Game.CONSTANTS.BALL_SPEED_NORMAL)],
            ['fastSpeedBtn', () => this.setBallSpeed(Game.CONSTANTS.BALL_SPEED_FAST)],
            ['longPaddleBtn', () => this.setPaddleLength(Game.CONSTANTS.PADDLE_LENGTH_LONG)],
            ['normalPaddleBtn', () => this.setPaddleLength(Game.CONSTANTS.PADDLE_LENGTH_NORMAL)],
            ['shortPaddleBtn', () => this.setPaddleLength(Game.CONSTANTS.PADDLE_LENGTH_SHORT)],
            ['hellModeBtn', () => this.setHellMode()],
            ['randomModeBtn', () => this.toggleRandomMode()],
            ['startBtn', () => this.startGame()]
        ];

        buttonConfigs.forEach(([id, handler]) => {
            document.getElementById(id).addEventListener('click', handler);
        });
    }

    setBallSpeed(speed) {
        if (this.gameRunning) return;
        this.ballVelocityX = this.ballVelocityY = speed;
        this.hellMode = (speed === Game.CONSTANTS.BALL_SPEED_HELL);
        this.randomMode = false;
        this.paddleVisible = !this.hellMode;
    }

    setPaddleLength(length) {
        if (this.gameRunning || this.hellMode) return;
        this.paddleWidth = length;
        this.paddleVisible = true;
        this.randomMode = false;
    }

    setHellMode() {
        if (this.gameRunning) return;
        this.hellMode = true;
        this.randomMode = false;
        this.ballVelocityX = this.ballVelocityY = Game.CONSTANTS.BALL_SPEED_HELL;
        this.paddleWidth = Game.CONSTANTS.PADDLE_LENGTH_NORMAL;
        this.paddleVisible = false;
    }

    toggleRandomMode() {
        if (this.gameRunning) return;
        this.randomMode = !this.randomMode;
        this.hellMode = false;

        if (this.randomMode) {
            this.showMessage("Random mode ON", 2);
            this.changeRandomSettings();
        } else {
            this.showMessage("Random mode OFF", 2);
            this.setBallSpeed(Game.CONSTANTS.BALL_SPEED_NORMAL);
            this.setPaddleLength(Game.CONSTANTS.PADDLE_LENGTH_NORMAL);
        }
    }

    changeRandomSettings() {
        if (!this.randomMode) return;

        const speeds = [Game.CONSTANTS.BALL_SPEED_SLOW, Game.CONSTANTS.BALL_SPEED_NORMAL, Game.CONSTANTS.BALL_SPEED_FAST];
        const lengths = [Game.CONSTANTS.PADDLE_LENGTH_SHORT, Game.CONSTANTS.PADDLE_LENGTH_NORMAL, Game.CONSTANTS.PADDLE_LENGTH_LONG];

        this.ballVelocityX = this.ballVelocityY = speeds[Math.floor(Math.random() * 3)] * (1 + Math.random());
        this.paddleWidth = lengths[Math.floor(Math.random() * 3)] * (1 + Math.random());
        this.paddleVisible = true;
    }

    init() {
        this.updateInfo();
        this.gameLoop();
    }

    gameLoop() {
        if (this.gameRunning) {
            this.moveBall();
            this.checkCollisions();

            if (this.randomMode && Math.random() < 0.05) {
                this.changeRandomSettings();
            }
        }

        this.render();
        this.updateInfo();
        setTimeout(() => this.gameLoop(), 10);
    }

    moveBall() {
        const randomFactor = this.randomMode ? (0.7 + Math.random() * 0.3) : 1.0;
        this.ballX += this.ballVelocityX * Math.cos(this.ballAngle) * randomFactor;
        this.ballY -= this.ballVelocityY * Math.sin(this.ballAngle) * randomFactor;
    }

    checkCollisions() {
        const C = Game.CONSTANTS;

        // 墙壁碰撞
        if (this.ballX < C.BALL_SIZE_RADIUS + 1 || this.ballX > C.SCREEN_WIDTH - C.BALL_SIZE_RADIUS - 1) {
            this.ballAngle = Math.PI - this.ballAngle;
            this.ballX = Math.max(C.BALL_SIZE_RADIUS + 1, Math.min(C.SCREEN_WIDTH - C.BALL_SIZE_RADIUS - 1, this.ballX));
            if (this.randomMode) this.ballAngle += (Math.random() * 0.2 - 0.1);
        }

        if (this.ballY < C.BALL_SIZE_RADIUS + 1) {
            this.ballAngle = -this.ballAngle;
            this.ballY = C.BALL_SIZE_RADIUS + 1;
            if (this.randomMode) this.ballAngle += (Math.random() * 0.2 - 0.1);
        }

        // 挡板碰撞检测
        if (this.ballX + C.BALL_SIZE_RADIUS > this.paddleX &&
            this.ballX - C.BALL_SIZE_RADIUS < this.paddleX + this.paddleWidth &&
            this.ballY + C.BALL_SIZE_RADIUS > C.PADDLE_START_Y &&
            this.ballY - C.BALL_SIZE_RADIUS < C.PADDLE_START_Y + C.PADDLE_HEIGHT_CONSTANT) {

            let hitFactor = (this.ballX - this.paddleX) / this.paddleWidth - 0.5;
            if (this.randomMode) hitFactor += (Math.random() - 0.5) / 2.0;

            this.ballAngle = hitFactor === 0 ? Math.PI / 2 :
                hitFactor < 0 ? Math.PI - Math.max(hitFactor * -Math.PI / 2, Math.PI / 6) :
                    Math.max(hitFactor * Math.PI / 2, Math.PI / 6);

            if (this.randomMode) this.ballAngle += (Math.random() * 0.2 - 0.1);
            this.ballY = C.PADDLE_START_Y - C.BALL_SIZE_RADIUS;
            this.score += this.getScoreIncrement();
        }

        // 砖块碰撞
        for (let i = 0; i < C.TOTAL_BRICK_ROWS; i++) {
            for (let j = 0; j < C.TOTAL_BRICK_COLUMNS; j++) {
                const brick = this.bricks[i][j];
                if (brick.visible &&
                    this.ballX + C.BALL_SIZE_RADIUS > brick.x &&
                    this.ballX - C.BALL_SIZE_RADIUS < brick.x + brick.width &&
                    this.ballY + C.BALL_SIZE_RADIUS > brick.y &&
                    this.ballY - C.BALL_SIZE_RADIUS < brick.y + brick.height) {

                    this.handleBrickCollision(brick);
                    brick.visible = false;
                    this.score += this.getScoreIncrement();
                    return;
                }
            }
        }

        // 游戏结束检查
        if (this.ballY > C.SCREEN_HEIGHT - C.BALL_SIZE_RADIUS) {
            this.gameOver();
        } else if (this.bricks.every(row => row.every(brick => !brick.visible))) {
            this.victory();
        }
    }

    handleBrickCollision(brick) {
        const C = Game.CONSTANTS;
        const overlapLeft = this.ballX + C.BALL_SIZE_RADIUS - brick.x;
        const overlapRight = brick.x + brick.width - (this.ballX - C.BALL_SIZE_RADIUS);
        const overlapTop = this.ballY + C.BALL_SIZE_RADIUS - brick.y;
        const overlapBottom = brick.y + brick.height - (this.ballY - C.BALL_SIZE_RADIUS);

        const minOverlap = Math.min(overlapLeft, overlapRight, overlapTop, overlapBottom);

        if (minOverlap === overlapLeft || minOverlap === overlapRight) {
            this.ballAngle = Math.PI - this.ballAngle;
        } else {
            this.ballAngle = -this.ballAngle;
        }

        if (this.randomMode) this.ballAngle += (Math.random() * 0.2 - 0.1);

        // 防止球卡住
        if (minOverlap === overlapLeft) this.ballX = brick.x - C.BALL_SIZE_RADIUS - 1;
        else if (minOverlap === overlapRight) this.ballX = brick.x + brick.width + C.BALL_SIZE_RADIUS + 1;
        else if (minOverlap === overlapTop) this.ballY = brick.y - C.BALL_SIZE_RADIUS - 1;
        else if (minOverlap === overlapBottom) this.ballY = brick.y + brick.height + C.BALL_SIZE_RADIUS + 1;
    }

    getScoreIncrement() {
        let baseScore = 1;
        const C = Game.CONSTANTS;

        if (this.hellMode || !this.paddleVisible) baseScore += 6;
        if (this.randomMode) baseScore += Math.floor(Math.random() * 10);

        // 简化速度分数计算
        const speedScores = {
            [C.BALL_SPEED_SLOW]: 1,
            [C.BALL_SPEED_NORMAL]: 2,
            [C.BALL_SPEED_FAST]: 3
        };
        baseScore += speedScores[Math.round(this.ballVelocityX)] || 0;

        // 简化挡板分数计算
        const paddleScores = {
            [C.PADDLE_LENGTH_LONG]: 1,
            [C.PADDLE_LENGTH_NORMAL]: 2,
            [C.PADDLE_LENGTH_SHORT]: 3
        };
        baseScore += paddleScores[Math.round(this.paddleWidth)] || 0;

        return baseScore;
    }

    startGame() {
        if (this.gameRunning) return;
        this.reset();

        let countdownTime = 3;
        this.countdown = setInterval(() => {
            if (countdownTime > 0) {
                this.showMessage(`Starting in ${countdownTime}...`, 0);
                countdownTime--;
            } else {
                this.showMessage("GO!", 1);
                clearInterval(this.countdown);
                this.gameRunning = true;
            }
        }, 1000);
    }

    reset() {
        const C = Game.CONSTANTS;
        this.ballX = C.SCREEN_WIDTH / 2;
        this.ballY = C.SCREEN_HEIGHT / 2;
        this.ballAngle = Math.PI / 4;
        this.paddleX = C.SCREEN_WIDTH / 2 - this.paddleWidth / 2;
        this.score = 0;
        this.initBricks();
    }

    resetModes() {
        const C = Game.CONSTANTS;
        this.randomMode = false;
        this.hellMode = false;
        this.ballVelocityX = this.ballVelocityY = C.BALL_SPEED_NORMAL;
        this.paddleWidth = C.PADDLE_LENGTH_NORMAL;
        this.paddleVisible = true;
    }

    gameOver() {
        this.gameRunning = false;
        this.showMessage(`Game Over!<br>Score: ${this.score}<br><br>Click Start to restart`, 999);
        this.resetModes();
    }

    victory() {
        this.gameRunning = false;
        this.showMessage(`Victory!<br>All Bricks Destroyed!<br>Score: ${this.score}<br><br>Click Start to play again`, 999);
        this.resetModes();
    }

    render() {
        const C = Game.CONSTANTS;
        this.ctx.clearRect(0, 0, C.SCREEN_WIDTH, C.SCREEN_HEIGHT);

        // 绘制砖块
        this.bricks.forEach((row, i) => {
            row.forEach(brick => {
                if (!brick.visible) return;

                const gradient = this.ctx.createLinearGradient(brick.x, brick.y, brick.x, brick.y + brick.height);
                gradient.addColorStop(0, brick.color);
                gradient.addColorStop(1, Game.BRICK_BORDER_COLORS[i]);

                this.ctx.fillStyle = gradient;
                this.ctx.fillRect(brick.x, brick.y, brick.width, brick.height);
                this.ctx.strokeStyle = Game.BRICK_BORDER_COLORS[i];
                this.ctx.lineWidth = 1.5;
                this.ctx.strokeRect(brick.x, brick.y, brick.width, brick.height);
                this.ctx.fillStyle = 'rgba(255, 255, 255, 0.3)';
                this.ctx.fillRect(brick.x + 1, brick.y + 1, brick.width - 2, 3);
                this.ctx.fillStyle = 'rgba(0, 0, 0, 0.1)';
                this.ctx.fillRect(brick.x + 1, brick.y + brick.height - 3, brick.width - 2, 2);
            });
        });

        // 绘制球
        const ballGradient = this.ctx.createRadialGradient(this.ballX - 3, this.ballY - 3, 0, this.ballX, this.ballY, C.BALL_SIZE_RADIUS);
        ballGradient.addColorStop(0, '#666');
        ballGradient.addColorStop(1, '#222');
        this.ctx.beginPath();
        this.ctx.arc(this.ballX, this.ballY, C.BALL_SIZE_RADIUS, 0, 2 * Math.PI);
        this.ctx.fillStyle = ballGradient;
        this.ctx.fill();
        this.ctx.strokeStyle = '#000';
        this.ctx.lineWidth = 1;
        this.ctx.stroke();

        // 绘制挡板
        if (this.paddleVisible) {
            const paddleGradient = this.ctx.createLinearGradient(this.paddleX, C.PADDLE_START_Y, this.paddleX, C.PADDLE_START_Y + C.PADDLE_HEIGHT_CONSTANT);
            paddleGradient.addColorStop(0, '#555');
            paddleGradient.addColorStop(1, '#222');
            this.ctx.fillStyle = paddleGradient;
            this.ctx.fillRect(this.paddleX, C.PADDLE_START_Y, this.paddleWidth, C.PADDLE_HEIGHT_CONSTANT);
            this.ctx.strokeStyle = '#000';
            this.ctx.lineWidth = 1;
            this.ctx.strokeRect(this.paddleX, C.PADDLE_START_Y, this.paddleWidth, C.PADDLE_HEIGHT_CONSTANT);
            this.ctx.fillStyle = 'rgba(255, 255, 255, 0.3)';
            this.ctx.fillRect(this.paddleX + 1, C.PADDLE_START_Y + 1, this.paddleWidth - 2, 2);
        }
    }

    updateInfo() {
        this.scoreDisplay.textContent = `Score: ${this.score}`;
        this.speedDisplay.textContent = `Speed: ${Math.round(this.ballVelocityX)}`;
        this.paddleDisplay.textContent = this.hellMode ? 'Paddle: INVISIBLE' : `Paddle: ${Math.round(this.paddleWidth)}`;
    }

    showMessage(message, duration) {
        if (this.messageHider) clearTimeout(this.messageHider);
        this.statusMessage.innerHTML = message;
        this.statusMessage.classList.remove('hidden');

        if (duration > 0 && duration < 999) {
            this.messageHider = setTimeout(() => {
                this.statusMessage.classList.add('hidden');
            }, duration * 1000);
        }
    }
}